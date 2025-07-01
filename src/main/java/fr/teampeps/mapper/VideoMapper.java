package fr.teampeps.mapper;

import fr.teampeps.dto.VideoDto;
import fr.teampeps.models.Video;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VideoMapper {

    public VideoDto toVideoDto(Video video) {
        return VideoDto.builder()
                .id(video.getId())
                .title(video.getTitle())
                .link(video.getLink())
                .imageKey(video.getImageKey())
                .build();
    }

    public List<VideoDto> toVideoDtoList(List<Video> videos) {
        return videos.stream()
                .map(this::toVideoDto)
                .toList();
    }
}
