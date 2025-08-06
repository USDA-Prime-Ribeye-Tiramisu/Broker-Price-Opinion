package com.broker.price.opinion.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentsMade {

    private String activeCP1Comment;
    private String activeCP2Comment;
    private String activeCP3Comment;
    private String closedCP1Comment;
    private String closedCP2Comment;
    private String closedCP3Comment;
}