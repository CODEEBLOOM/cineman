package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.controller.request.ProvinceRequest;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.MovieTheaterEntity;
import com.codebloom.cineman.model.ProvinceEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import com.codebloom.cineman.repository.CinemaTheatersRepository;
import com.codebloom.cineman.repository.MovieTheaterRepository;
import com.codebloom.cineman.repository.ProvinceRepository;
import com.codebloom.cineman.repository.ShowTimeRepository;
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
    private final MovieTheaterRepository movieTheaterRepository;
    private final CinemaTheatersRepository cinemaTheatersRepository;
    private final ShowTimeRepository showTimeRepository;
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
        return provinceRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new DataNotFoundException("Province not found with id: " + id));
    }


    /**
     * Tìm tỉnh thành theo tên
     * @param provinceName tên tỉnh thành
     * @return ProvinceEntity
     */
    @Override
    public ProvinceEntity findByName(String provinceName) {
        return provinceRepository.findByNameAndActive(provinceName, true)
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
    @Transactional
    public ProvinceEntity update(Integer id, ProvinceRequest province) {
        provinceRepository.findByNameAndIdNot(province.getName(), id)
                .ifPresent(existingProvince -> {
                    throw new DataExistingException("Province already exist with name: " + province.getName());
                });
        provinceRepository.findByCodeAndIdNot(province.getCode(), id)
                .ifPresent(existingProvince -> {
                    throw new DataExistingException("Province already exist with code: " + province.getCode());
                });
        ProvinceEntity existingProvince = findById(id);
        modelMapper.map(province, existingProvince);

        return provinceRepository.save(existingProvince);
    }

    /**
     * Xóa mềm tỉnh thành
     * @param id id duy nhất của tỉnh thành
     */
    @Override
    @Transactional
    public void delete(Integer id) {
        softDeleteProvince(findById(id));
    }

    /**
     * Xóa tỉnh thành theo mã code của tỉnh
     * @param provinceCode mã code
     */
    @Override
    @Transactional
    public void deleteByCode(Integer provinceCode) {
        ProvinceEntity existingProvince = provinceRepository.findByCodeAndActive(provinceCode, true)
                .orElseThrow(() -> new DataNotFoundException("Province not found with code: " + provinceCode));
        softDeleteProvince(existingProvince);
    }

    /**
     * Hàm check trùng thông tin trong DB ném ra lỗi ngay nếu bắt gặp trùng
     * @param province ProvinceEntity
     */
    private void checkCodeAndName(ProvinceEntity province) {
        provinceRepository.findByName(province.getName())
                .ifPresent(existingProvince -> {
                    throw new DataExistingException("Province already exist with name: " + province.getName());
                });

        provinceRepository.findByCode(province.getCode())
                .ifPresent(existingProvince -> {
                    throw new DataExistingException("Province already exist with code: " + province.getCode());
                });
    }

    private void softDeleteProvince(ProvinceEntity province) {
        List<MovieTheaterEntity> movieTheaters = movieTheaterRepository.findAllByStatusAndProvince_Id(true, province.getId());
        movieTheaters.forEach(movieTheater -> movieTheater.setStatus(false));

        List<CinemaTheaterEntity> cinemaTheaters = movieTheaters.stream()
                .flatMap(movieTheater -> cinemaTheatersRepository
                        .findAllByStatusNotAndMovieTheater_MovieTheaterId(CinemaTheaterStatus.INVALID, movieTheater.getMovieTheaterId())
                        .stream())
                .toList();
        softDeleteCinemaTheaters(cinemaTheaters);

        province.setActive(false);
        movieTheaterRepository.saveAll(movieTheaters);
        provinceRepository.save(province);
    }

    private void softDeleteCinemaTheaters(List<CinemaTheaterEntity> cinemaTheaters) {
        if (cinemaTheaters.isEmpty()) {
            return;
        }

        List<ShowTimeEntity> showTimes = showTimeRepository.findAllByCinemaTheaterInAndStatusNot(cinemaTheaters, ShowTimeStatus.DELETED);
        showTimes.forEach(showTime -> showTime.setStatus(ShowTimeStatus.DELETED));
        cinemaTheaters.forEach(cinemaTheater -> cinemaTheater.setStatus(CinemaTheaterStatus.INVALID));

        showTimeRepository.saveAll(showTimes);
        cinemaTheatersRepository.saveAll(cinemaTheaters);
    }

}
