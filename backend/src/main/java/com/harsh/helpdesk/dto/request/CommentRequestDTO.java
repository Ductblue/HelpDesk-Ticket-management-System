package com.harsh.helpdesk.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDTO {

    @NotBlank(message = "Comment content is required")
    @Size(max=2000,message = "Content should not exceed 2000")
    private String content;
}
