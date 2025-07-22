package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.ProvinceRequest;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.ProvinceEntity;
import com.codebloom.cineman.repository.ProvinceRepository;
import com.codebloom.cineman.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PROVINCE-SERVICE")
public class ProvinceServiceImpl implements ProvinceService {

    private final ProvinceRepository provinceRepository;
    private final ModelMapper modelMapper;

    /**
     * Get tất cả các tỉnh có rạp chiếu Cineman
     * @return Danh sách các tỉnh
     */
    @Override
    public List<ProvinceEntity> findAll() {
        return provinceRepository.findAllByActive(true);
    }

    /**
     * Tìm kiếm thông tin của tỉnh có rạp chiếu theo Id
     * @param id id rạp chiếu
     * @return ProvinceEntity
     */
    @Override
    public ProvinceEntity findById(Integer id) {
        return provinceRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Province not found with id: " + id));
    }


    /**
     * Tìm tỉnh thành theo tên
     * @param provinceName tên tỉnh thành
     * @return ProvinceEntity
     */
    @Override
    public ProvinceEntity findByName(String provinceName) {
        return provinceRepository.findByName(provinceName)
                .orElseThrow(() -> new DataNotFoundException("Province not found with name: " + provinceName));
    }

    /**
     * Tạo mới một tỉnh thành có rạp chiếu
     * @param province ProvinceEntity
     * @return ProvinceEntity
     */
    @Override
    @Transactional
    public ProvinceEntity save(ProvinceRequest province) {
        ProvinceEntity provinceEntity = modelMapper.map(province, ProvinceEntity.class);
        checkCodeAndName(provinceEntity);
        provinceEntity.setActive(true);
        return provinceRepository.save(provinceEntity);
    }

    /**
     * Cập nhật thông tin tỉnh
     * @param province ProvinceEntity
     * @return ProvinceEntity
     */
    @Override
    public ProvinceEntity update(Integer id, ProvinceRequest province) {
        provinceRepository
                .findByNameAndCodeAndIdNot(province.getName(), province.getCode(), id)
                .ifPresent((provinceEntity)  -> {throw new DataExistingException("Province already exist");});
        ProvinceEntity existingProvince = findById(id);
        modelMapper.map(province, existingProvince);

        return provinceRepository.save(existingProvince);
    }

    /**
     * Xóa mềm tỉnh thành
     * @param id id duy nhất của tỉnh thành
     */
    @Override
    public void delete(Integer id) {
        ProvinceEntity existingProvince = findById(id);
        existingProvince.setActive(false);
        provinceRepository.save(existingProvince);
    }

    /**
     * Xóa tỉnh thành theo mã code của tỉnh
     * @param provinceCode mã code
     */
    @Override
    public void deleteByCode(Integer provinceCode) {
        ProvinceEntity existingProvince = provinceRepository.findByCode(provinceCode)
                .orElseThrow(() -> new DataNotFoundException("Province not found with code: " + provinceCode));
        existingProvince.setActive(false);
        provinceRepository.save(existingProvince);
    }

    /**
     * Hàm check trùng thông tin trong DB ném ra lỗi ngay nếu bắt gặp trùng
     * @param province ProvinceEntity
     */
    private void checkCodeAndName(ProvinceEntity province) {
        provinceRepository.findByName(province.getName())
                .ifPresent((provinceEntity ) -> {throw new DataExistingException("Province already exist with name: " + province.getName());});

        provinceRepository.findByCode(province.getCode())
                .ifPresent((provinceEntity -> {throw  new DataExistingException("Province already exist with code: " + province.getCode());}));
    }

}
