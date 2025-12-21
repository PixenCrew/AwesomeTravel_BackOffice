package renewal.awesome_travel_backoffice.image.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFileListResponse {
    private List<UploadedFileDetail> files;
    private Integer totalCount;
}



