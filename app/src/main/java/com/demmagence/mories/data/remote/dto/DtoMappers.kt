package com.demmagence.mories.data.remote.dto

import com.demmagence.mories.domain.model.*

fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title ?: "",
    overview = overview ?: "",
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage ?: 0.0,
    voteCount = voteCount ?: 0,
    releaseDate = releaseDate,
    genreIds = genreIds ?: emptyList(),
    popularity = popularity ?: 0.0,
    adult = adult ?: false,
    originalLanguage = originalLanguage ?: "",
    mediaType = mediaType ?: "movie"
)

fun TvDto.toDomain(): TvShow = TvShow(
    id = id,
    name = name ?: "",
    overview = overview ?: "",
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage ?: 0.0,
    voteCount = voteCount ?: 0,
    firstAirDate = firstAirDate,
    genreIds = genreIds ?: emptyList(),
    popularity = popularity ?: 0.0,
    originalLanguage = originalLanguage ?: "",
    mediaType = mediaType ?: "tv"
)

fun GenreDto.toDomain(): Genre = Genre(
    id = id,
    name = name
)

fun CastDto.toDomain(): Cast = Cast(
    id = id,
    name = name ?: "",
    character = character ?: "",
    profilePath = profilePath,
    order = order ?: 0
)

fun CrewDto.toDomain(): Crew = Crew(
    id = id,
    name = name ?: "",
    job = job ?: "",
    department = department ?: "",
    profilePath = profilePath
)

fun VideoDto.toDomain(): Video = Video(
    id = id,
    key = key ?: "",
    name = name ?: "",
    site = site ?: "",
    type = type ?: "",
    official = official ?: false
)

fun ReviewDto.toDomain(): Review = Review(
    id = id,
    author = author ?: authorDetails?.username ?: "Anonymous",
    content = content ?: "",
    rating = authorDetails?.rating,
    createdAt = createdAt,
    avatarPath = authorDetails?.avatarPath
)

fun SeasonDto.toDomain(): Season = Season(
    id = id,
    seasonNumber = seasonNumber ?: 0,
    name = name ?: "",
    overview = overview,
    posterPath = posterPath,
    episodeCount = episodeCount ?: 0,
    airDate = airDate
)

fun EpisodeDto.toDomain(): Episode = Episode(
    id = id,
    episodeNumber = episodeNumber ?: 0,
    seasonNumber = seasonNumber ?: 0,
    name = name ?: "",
    overview = overview,
    stillPath = stillPath,
    airDate = airDate,
    runtime = runtime,
    voteAverage = voteAverage ?: 0.0
)

fun MovieDetailDto.toDomain(): MovieDetail = MovieDetail(
    id = id,
    title = title ?: "",
    overview = overview ?: "",
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage ?: 0.0,
    voteCount = voteCount ?: 0,
    releaseDate = releaseDate,
    runtime = runtime,
    tagline = tagline,
    status = status,
    genres = genres?.map { it.toDomain() } ?: emptyList(),
    cast = credits?.cast?.map { it.toDomain() } ?: emptyList(),
    crew = credits?.crew?.map { it.toDomain() } ?: emptyList(),
    similar = similar?.results?.map { it.toDomain() } ?: emptyList(),
    reviews = reviews?.results?.map { it.toDomain() } ?: emptyList(),
    videos = videos?.results?.map { it.toDomain() } ?: emptyList(),
    popularity = popularity ?: 0.0
)

fun TvDetailDto.toDomain(): TvShowDetail = TvShowDetail(
    id = id,
    name = name ?: "",
    overview = overview ?: "",
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage ?: 0.0,
    voteCount = voteCount ?: 0,
    firstAirDate = firstAirDate,
    lastAirDate = lastAirDate,
    numberOfSeasons = numberOfSeasons ?: 0,
    numberOfEpisodes = numberOfEpisodes ?: 0,
    status = status,
    tagline = tagline,
    genres = genres?.map { it.toDomain() } ?: emptyList(),
    seasons = seasons?.map { it.toDomain() } ?: emptyList(),
    cast = credits?.cast?.map { it.toDomain() } ?: emptyList(),
    crew = credits?.crew?.map { it.toDomain() } ?: emptyList(),
    similar = similar?.results?.map { it.toDomain() } ?: emptyList(),
    reviews = reviews?.results?.map { it.toDomain() } ?: emptyList(),
    videos = videos?.results?.map { it.toDomain() } ?: emptyList(),
    popularity = popularity ?: 0.0
)

fun SearchResultDto.toMovie(): Movie = Movie(
    id = id,
    title = title ?: name ?: "",
    overview = overview ?: "",
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage ?: 0.0,
    voteCount = voteCount ?: 0,
    releaseDate = releaseDate ?: firstAirDate,
    genreIds = genreIds ?: emptyList(),
    popularity = popularity ?: 0.0,
    mediaType = mediaType ?: "movie"
)

fun SearchResultDto.toTvShow(): TvShow = TvShow(
    id = id,
    name = name ?: title ?: "",
    overview = overview ?: "",
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage ?: 0.0,
    voteCount = voteCount ?: 0,
    firstAirDate = firstAirDate ?: releaseDate,
    genreIds = genreIds ?: emptyList(),
    popularity = popularity ?: 0.0,
    mediaType = mediaType ?: "tv"
)
