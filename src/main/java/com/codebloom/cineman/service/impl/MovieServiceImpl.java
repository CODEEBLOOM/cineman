package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.constant.MovieStatus;
import com.codebloom.cineman.controller.request.*;
import com.codebloom.cineman.controller.response.MetaResponse;
import com.codebloom.cineman.controller.response.MoviePageableResponse;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.*;
import com.codebloom.cineman.repository.MovieGenresRepository;
import com.codebloom.cineman.repository.MovieParticipantRepository;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.MovieRoleRepository;
import com.codebloom.cineman.repository.MovieStatusRepository;
import com.codebloom.cineman.service.GenreService;
import com.codebloom.cineman.service.MovieService;
import com.codebloom.cineman.service.MovieStatusService;
import com.codebloom.cineman.service.ParticipantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {
    private static final Set<String> DIRECTOR_ROLE_NAMES = Set.of("director", "daodien");
    private static final Set<String> CAST_ROLE_NAMES = Set.of("cast", "actor", "dienvien");

    private final MovieRepository movieRepository;
    private final MovieStatusRepository movieStatusRepository;
    private final MovieStatusService movieStatusService;
    private final ModelMapper modelMapper;
    private final GenreService genreService;
    private final MovieGenresRepository movieGenresRepository;
    private final MovieParticipantRepository movieParticipantRepository;
    private final MovieRoleRepository movieRoleRepository;
    private final ParticipantService participantService;

    /**
     * Find all movies
     * @param request MoviePageQueryRequest
     * @return request
     */
    @Override
    public MoviePageableResponse findAllByPage(MoviePageQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        if (request.getStatus().equals("ALL")) {
            MovieStatusEntity movieStatus = movieStatusRepository.findById(MovieStatus.MOVIE_STATUS_CNS)
                    .orElseThrow(() -> new DataNotFoundException("Movie status not found"));
            return movieToMoviePageableResponse(movieRepository.findAllByStatusNot(movieStatus, pageable));
        }
        MovieStatusEntity movieStatus = movieStatusRepository.findById(request.getStatus())
                .orElseThrow(() -> new DataNotFoundException("Movie status not found"));
        Page<MovieEntity> moviePage = movieRepository.findAllByStatus(movieStatus, pageable);
        return movieToMoviePageableResponse(moviePage);
    }

    /**
     * Find all movies
     * @param request MoviePageQueryRequest
     * @param movieTheaterId Integer
     * @return MoviePageableResponse
     */
    @Override
    public MoviePageableResponse findAllByPageAndFilter(MoviePageQueryRequest request, Integer movieTheaterId) {
        log.info("movieTheaterId: {}", movieTheaterId);
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        String statusFilter = normalizeMovieStatusFilter(request.getStatus());
        Page<MovieEntity> page;
        if (MovieStatus.MOVIE_STATUS_DB.equals(statusFilter)) {
            page = movieRepository.findAllDistinctDbOrSpecialMoviesByMovieTheaterId(
                    movieTheaterId,
                    MovieStatus.MOVIE_STATUS_DB,
                    pageable
            );
        } else if (MovieStatus.MOVIE_STATUS_DC.equals(statusFilter)) {
            page = movieRepository.findAllDistinctMoviesByMovieTheaterIdAndStatusWithShowTime(
                    movieTheaterId,
                    MovieStatus.MOVIE_STATUS_DC,
                    pageable
            );
        } else if (statusFilter != null) {
            MovieStatusEntity movieStatus = movieStatusRepository.findById(statusFilter)
                    .orElseThrow(() -> new DataNotFoundException("Movie status not found"));
            page = movieRepository.findAllByStatusAndMovieTheaterMapping(movieStatus, movieTheaterId, pageable);
        } else {
            page = movieRepository.findAllDistinctMoviesByMovieTheaterId(
                    movieTheaterId,
                    pageable
            );
        }
        log.info("end findAllByPageAndFilter");
        return movieToMoviePageableResponse(page);
    }

    /**
     * Find all movies
     * @return List<MovieResponse>
     */
    @Override
    public List<MovieResponse> findAll() {
        List<MovieEntity> movies = movieRepository.findAll();
        List<MovieResponse> movieResponses = new ArrayList<>();
        movies.forEach(movie -> movieResponses.add(movieToMovieResponse(movie)));
        return movieResponses;
    }


    /**
     * Find movie
     * @param id Integer
     * @return MovieResponses
     */
    @Override
    public MovieResponse findById(Integer id) {
        log.info("Movie found: {}", id);
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Movie not found with id: " + id));
        if(movie.getStatus().getStatusId().equals(MovieStatus.MOVIE_STATUS_CNS)){
            throw new IllegalArgumentException("Movie is not available !");
        }
        log.info("Movie director found: {}", movie.getMovieParticipants());
        return movieToMovieResponse(movie);
    }

    /**
     * Find movie
     * @param id Integer
     * @param isEntity boolean
     * @return MovieEntity
     */
    @Override
    public MovieEntity findById(Integer id, boolean isEntity) {
        return movieRepository.findById(id)
                .orElseThrow(() ->new DataNotFoundException("Movie not found with id: " + id));
    }


    /**
     * Create movie
     * @param request MovieCreationRequest
     * @return MovieEntity
     */
    @Override
    @Transactional
    public MovieEntity save(MovieCreationRequest request) {
//        MovieStatusEntity status ;
        MovieStatusEntity movieStatus = movieStatusRepository.findById(request.getStatus())
                .orElseThrow(() -> new DataNotFoundException("Movie status not found"));
//        if(movieStatus.isEmpty()){
//            MovieStatusEntity movieStatusEntity = new MovieStatusEntity();
//            movieStatusEntity.setStatusId(MovieStatus.MOVIE_STATUS_DB);
//            movieStatusEntity.setName("Đặc biệt");
//            movieStatusEntity.setDescription("Trạng thái dành cho các bộ phim chưa có xuất chiếu tai rạp !");
//            movieStatusEntity.setActive(true);
//            status = movieStatusRepository.save(movieStatusEntity);
//        }else {
//            status = movieStatus.get();
//        }
        MovieEntity movie = modelMapper.map(request, MovieEntity.class);
        movie.setStatus(movieStatus);
        movie = movieRepository.save(movie);

        // Cho nó thể loại phim //
        movie.setMovieGenres(replaceMovieGenres(movie, request.getGenres()));
        movie.setMovieParticipants(replaceMovieParticipants(movie, request.getDirectors(), request.getCasts()));
        log.info("Movie saved: {}", movie);
        return movie;
    }


    /**
     * Update movie
     * @param request MovieUpdateRequest
     * @return MovieResponse
     */
    @Override
    @Transactional
    public MovieResponse update(MovieUpdateRequest request) {
        MovieEntity movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new DataNotFoundException("Movie not found with id: " + request.getMovieId()));
        MovieStatusEntity movieStatus = movieStatusRepository.findById(request.getStatus())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy trạng thái phim id: " + request.getStatus()));

        movie.setTitle(request.getTitle());
        movie.setSynopsis(request.getSynopsis());
        movie.setDetailDescription(request.getDetailDescription());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setEndDate(request.getEndDate());
        movie.setLanguage(request.getLanguage());
        movie.setDuration(request.getDuration());
        movie.setAge(request.getAge());
        movie.setTrailerLink(request.getTrailerLink());
        movie.setPosterImage(request.getPosterImage());
        movie.setBannerImage(request.getBannerImage());
        movie.setUpdatedAt(new Date());
        movie.setStatus(movieStatus);

        movie.setMovieGenres(replaceMovieGenres(movie, request.getGenres()));
        movie.setMovieParticipants(replaceMovieParticipants(movie, request.getDirectors(), request.getCasts()));

        movie = movieRepository.save(movie);
        return movieToMovieResponse(movie);
    }


    /**
     * Delete movie
     * @param id Integer
     */
    @Override
    public void delete(Integer id) {
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Movie not found with id: " + id));
        if (
                movie.getStatus().getStatusId().equals(MovieStatus.MOVIE_STATUS_DC)
        ) {
            throw new ConflictException("Phim đang chiếu không thể xóa !");
        }

        MovieStatusEntity cancelledStatus = movieStatusRepository.findById(MovieStatus.MOVIE_STATUS_CNS)
                .orElseGet(() -> {
                    MovieStatusEntity newStatus = new MovieStatusEntity();
                    newStatus.setStatusId(MovieStatus.MOVIE_STATUS_CNS);
                    newStatus.setName("Đã hủy");
                    newStatus.setDescription("Phim đã bị hủy ");
                    return movieStatusRepository.save(newStatus);
                });

        movie.setStatus(cancelledStatus);
        movieRepository.save(movie);
    }


    /**
     * Convert MovieEntity to MovieResponse
     * @param movie MovieEntity
     * @return MovieResponse
     */
    private MovieResponse movieToMovieResponse(MovieEntity movie) {
        List<GenresEntity> genres = new ArrayList<>();
        List<ParticipantEntity> directors = new ArrayList<>();
        List<ParticipantEntity> casts = new ArrayList<>();
        if (movie.getMovieGenres() != null) {
            movie.getMovieGenres().forEach(movieGenre -> genres.add(movieGenre.getGenres()));
        }
        if (movie.getMovieParticipants() != null) {
            movie.getMovieParticipants().forEach((movieParticipant) -> {
                if (isDirectorRole(movieParticipant.getMovieRole())) {
                    directors.add(movieParticipant.getParticipant());
                } else if (isCastRole(movieParticipant.getMovieRole())) {
                    casts.add(movieParticipant.getParticipant());
                }
            });
        }
        return MovieResponse.builder()
                .movieId(movie.getMovieId())
                .status(movie.getStatus().getStatusId())
                .synopsis(movie.getSynopsis())
                .detailDescription(movie.getDetailDescription())
                .title(movie.getTitle())
                .releaseDate(movie.getReleaseDate())
                .endDate(movie.getEndDate())
                .language(movie.getLanguage())
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .age(movie.getAge())
                .trailerLink(movie.getTrailerLink())
                .posterImage(movie.getPosterImage())
                .bannerImage(movie.getBannerImage())
                .directors(directors)
                .casts(casts)
                .genres(genres)
                .build();
    }

    private boolean isDirectorRole(MovieRoleEntity movieRole) {
        String normalizedRoleName = normalizeRoleName(movieRole);
        return normalizedRoleName.equals("director")
                || normalizedRoleName.equals("daodien");
    }

    private boolean isCastRole(MovieRoleEntity movieRole) {
        String normalizedRoleName = normalizeRoleName(movieRole);
        return normalizedRoleName.equals("cast")
                || normalizedRoleName.equals("actor")
                || normalizedRoleName.equals("dienvien");
    }

    private String normalizeMovieStatusFilter(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        String normalizedStatus = status.trim().toUpperCase(Locale.ROOT);
        return "ALL".equals(normalizedStatus) ? null : normalizedStatus;
    }

    private String normalizeRoleName(MovieRoleEntity movieRole) {
        if (movieRole == null || movieRole.getName() == null) {
            return "";
        }
        String normalized = Normalizer.normalize(movieRole.getName(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized
                .toLowerCase(Locale.ROOT)
                .replace('đ', 'd')
                .replaceAll("\\s+", "");
    }

    private Set<MovieGenresEntity> replaceMovieGenres(MovieEntity movie, List<Integer> genreIds) {
        movieGenresRepository.deleteAllByMovie(movie);
        movieGenresRepository.flush();

        Set<MovieGenresEntity> movieGenres = new LinkedHashSet<>();
        for (Integer genreId : new LinkedHashSet<>(genreIds)) {
            GenresEntity genresEntity = genreService.findById(genreId);

            MovieGenresEntity movieGenresEntity = new MovieGenresEntity();
            movieGenresEntity.setGenres(genresEntity);
            movieGenresEntity.setMovie(movie);

            movieGenresRepository.save(movieGenresEntity);
            movieGenres.add(movieGenresEntity);
        }
        return movieGenres;
    }

    private Set<MovieParticipantEntity> replaceMovieParticipants(
            MovieEntity movie,
            List<Integer> directorIds,
            List<Integer> castIds
    ) {
        movieParticipantRepository.deleteAllByMovie(movie);
        movieParticipantRepository.flush();

        Set<MovieParticipantEntity> movieParticipants = new LinkedHashSet<>();
        MovieRoleEntity directorRole = resolveMovieRole(DIRECTOR_ROLE_NAMES, "director");
        MovieRoleEntity castRole = resolveMovieRole(CAST_ROLE_NAMES, "cast/actor");

        addMovieParticipants(movie, new LinkedHashSet<>(directorIds), directorRole, movieParticipants);
        addMovieParticipants(movie, new LinkedHashSet<>(castIds), castRole, movieParticipants);
        return movieParticipants;
    }

    private void addMovieParticipants(
            MovieEntity movie,
            Collection<Integer> participantIds,
            MovieRoleEntity movieRole,
            Set<MovieParticipantEntity> movieParticipants
    ) {
        for (Integer participantId : participantIds) {
            ParticipantEntity participant = participantService.findById(participantId);

            MovieParticipantEntity movieParticipant = new MovieParticipantEntity();
            movieParticipant.setMovie(movie);
            movieParticipant.setParticipant(participant);
            movieParticipant.setMovieRole(movieRole);

            movieParticipantRepository.save(movieParticipant);
            movieParticipants.add(movieParticipant);
        }
    }

    private MovieRoleEntity resolveMovieRole(Set<String> supportedRoleNames, String roleLabel) {
        return movieRoleRepository.findAllByActive(true).stream()
                .filter(movieRole -> supportedRoleNames.contains(normalizeRoleName(movieRole)))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("Movie role not found with name: " + roleLabel));
    }

    /**
     * Convert MovieEntity to MoviePageableResponse
     * @param page Page<MovieEntity>
     * @return MoviePageableResponse
     */
    @Override
    public MoviePageableResponse movieToMoviePageableResponse(Page<MovieEntity> page) {
        List<MovieResponse> movieResponses = new ArrayList<>();
        page.getContent().forEach(movieEntity -> movieResponses.add(movieToMovieResponse(movieEntity)));
        MetaResponse metaResponse = MetaResponse.builder()
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements((int) page.getTotalElements())                .build();
        metaResponse.setCurrentPage(page.getNumber());
        metaResponse.setTotalPages(page.getTotalPages());
        metaResponse.setPageSize(page.getSize());
        metaResponse.setTotalElements(page.getNumberOfElements());

        MoviePageableResponse moviePageableResponse = new MoviePageableResponse();
        moviePageableResponse.setMeta(metaResponse);
        moviePageableResponse.setMovies(movieResponses);
        return moviePageableResponse;
    }

//    @Override
//    @Transactional
//    public MovieResponseNew createMovie(MovieCreationRequestNew movie) {
//         MovieStatusEntity movieStatus = movieStatusRepository.findById(movie.getStatus())
//                .orElseThrow(() -> new DataNotFoundException("Movie status not found with id: " + movie.getStatus()));
//
//        MovieEntity movieEntity = MovieEntity.builder()
//                .title(movie.getTitle())
//                .synopsis(movie.getSynopsis())
//                .releaseDate(movie.getReleaseDate())
//                .endDate(movie.getEndDate())
//                .language(movie.getLanguage())
//                .duration(movie.getDuration())
//                .age(movie.getAge())
//                .trailerLink(movie.getTrailerLink())
//                .posterImage(movie.getPosterImage())
//                .bannerImage(movie.getBannerImage())
//                .detailDescription(movie.getDescription())
//                .rating(Rating.GOOD)
//                .status(movieStatus)
//                .build();
//        movieRepository.save(movieEntity);
//
//        // Genres
//        if (movie.getGenres() != null || movie.getGenres().size() > 0) {
//            for(Integer genreId: movie.getGenres()){
//                MovieGenreRequest request = MovieGenreRequest.builder()
//                        .movieId(movieEntity.getMovieId())
//                        .genreId(genreId)
//                        .build();
//                movieGenreService.addMovieGenre(request);
//            }
//        }
//
//        MovieRoleEntity cast = movieRoleRepository.findByNameAndActive("cast", true)
//                .orElseThrow(() -> new DataNotFoundException("Movie role not found with name: cast"));
//        // Casts
//        if(movie.getCasts() != null || movie.getCasts().size() > 0){
//            for(Integer castId: movie.getCasts()){
//                MovieParticipantRequest request = MovieParticipantRequest.builder()
//                        .movieId(movieEntity.getMovieId())
//                        .participantId(castId)
//                        .movieRoleId(cast.getMovieRoleId())
//                        .build();
//                movieDirectorService.addParticipantMovie(request);
//            }
//        }
//
//        MovieRoleEntity director = movieRoleRepository.findByNameAndActive("director", true)
//                .orElseThrow(() -> new DataNotFoundException("Movie role not found with name: director"));
//
//        // Directors
//        if(movie.getDirectors() != null || movie.getDirectors().size() > 0){
//            for(Integer directorId: movie.getDirectors()){
//                MovieParticipantRequest request = MovieParticipantRequest.builder()
//                        .movieId(movieEntity.getMovieId())
//                        .participantId(directorId)
//                        .movieRoleId(director.getMovieRoleId())
//                        .build();
//                movieDirectorService.addParticipantMovie(request);
//            }
//        }
//
//        return MovieResponseNew.builder()
//                .movieId(movieEntity.getMovieId())
//                .title(movieEntity.getTitle())
//                .synopsis(movie.getSynopsis())
//                .releaseDate(movieEntity.getReleaseDate())
//                .endDate(movieEntity.getEndDate())
//                .language(movieEntity.getLanguage())
//                .duration(movieEntity.getDuration())
//                .age(movieEntity.getAge())
//                .trailerLink(movieEntity.getTrailerLink())
//                .posterImage(movieEntity.getPosterImage())
//                .bannerImage(movieEntity.getBannerImage())
//                .detailDescription(movieEntity.getDetailDescription())
//                .status(movieEntity.getStatus().getName())
//                .build();
//    }

}
