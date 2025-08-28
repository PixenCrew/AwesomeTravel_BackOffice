package renewal.awesome_travel_backoffice.comment.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;

import renewal.awesome_travel_backoffice.product.entity.Product;
import renewal.awesome_travel_backoffice.user.entity.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class Comment {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private int rating; // ⭐ 1~5점

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommentReport> reports = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Comment create(User writer, Product product, String content, int rating) {
        Comment comment = new Comment();
        comment.writer = writer;
        comment.product = product;
        comment.content = content;
        comment.rating = rating;
        comment.createdAt = LocalDateTime.now();
        return comment;
    }

    public void update(String content, int rating) {
        this.content = content;
        this.rating = rating;
        this.updatedAt = LocalDateTime.now();
    }
}
