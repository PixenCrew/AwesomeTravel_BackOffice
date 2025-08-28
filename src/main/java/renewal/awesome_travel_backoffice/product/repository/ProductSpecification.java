package renewal.awesome_travel_backoffice.product.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import renewal.awesome_travel_backoffice.product.entity.Product;
import renewal.awesome_travel_backoffice.tour.entity.Tour;

public class ProductSpecification {

    // title LIKE %title%
    public static Specification<Product> titleContains(String title) {
        return (root, query, builder) -> (title == null || title.isEmpty())
                ? null
                : builder.like(builder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    // price BETWEEN min AND max
    public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, builder) -> {
            if (min != null && max != null) {
                return builder.between(root.get("price"), min, max);
            } else if (min != null) {
                return builder.greaterThanOrEqualTo(root.get("price"), min);
            } else if (max != null) {
                return builder.lessThanOrEqualTo(root.get("price"), max);
            }
            return null;
        };
    }

    // info LIKE %keyword%
    public static Specification<Product> infoContains(String keyword) {
        return (root, query, builder) -> {
            if (keyword == null || keyword.isEmpty())
                return null;
            query.distinct(true); // 중복 Product 방지
            Join<Product, Product.Info> infoJoin = root.join("info");
            return builder.or(
                    builder.like(builder.lower(infoJoin.get("title")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(infoJoin.get("content")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(infoJoin.get("appendix")), "%" + keyword.toLowerCase() + "%"));
        };
    }

    public static Specification<Product> avgBetween(BigDecimal from, BigDecimal to) {
    return (root, query, builder) -> {
        // star1 ~ star5 칼럼 꺼내오기
        Expression<BigDecimal> star1 = root.get("star1").as(BigDecimal.class);
        Expression<BigDecimal> star2 = root.get("star2").as(BigDecimal.class);
        Expression<BigDecimal> star3 = root.get("star3").as(BigDecimal.class);
        Expression<BigDecimal> star4 = root.get("star4").as(BigDecimal.class);
        Expression<BigDecimal> star5 = root.get("star5").as(BigDecimal.class);

        // 분자 = (1*star1 + 2*star2 + ... + 5*star5)
        Expression<Number> numerator = builder.sum(
            builder.sum(
                builder.sum(
                    builder.sum(
                        builder.prod(star1, 1),
                        builder.prod(star2, 2)
                    ),
                    builder.prod(star3, 3)
                ),
                builder.prod(star4, 4)
            ),
            builder.prod(star5, 5)
        );

        // 분모 = (star1 + star2 + ... + star5)
        Expression<BigDecimal> denominator = builder.sum(
            builder.sum(
                builder.sum(
                    builder.sum(star1, star2),
                    star3
                ),
                star4
            ),
            star5
        );

        // 평균 = 분자 / 분모 (BigDecimal 캐스팅)
        Expression<BigDecimal> avg = builder.quot(numerator, denominator).as(BigDecimal.class);

        // 조건 처리
        if (from != null && to != null) {
            return builder.between(avg,  builder.literal(from), builder.literal(to));
        } else if (from != null) {
            return builder.greaterThanOrEqualTo(avg,  builder.literal(from));
        } else if (to != null) {
            return builder.lessThanOrEqualTo(avg,  builder.literal(from));
        }
        return null;
    };
}


    // Tour.country = ?
    public static Specification<Product> tourCountryEquals(String country) {
        return (root, query, builder) -> {
            if (country == null || country.isEmpty())
                return null;
            Join<Product, Tour> tour = root.join("tour", JoinType.LEFT);
            return builder.equal(builder.lower(tour.get("country")), country.toLowerCase());
        };
    }

    // Tour.startDate BETWEEN from AND to
    public static Specification<Product> tourStartDateBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            Join<Product, Tour> tour = root.join("tour", JoinType.LEFT);
            if (from != null && to != null) {
                return builder.between(tour.get("startDate"), from, to);
            } else if (from != null) {
                return builder.greaterThanOrEqualTo(tour.get("startDate"), from);
            } else if (to != null) {
                return builder.lessThanOrEqualTo(tour.get("startDate"), to);
            }
            return null;
        };
    }

    // Tour.endDate BETWEEN from AND to
    public static Specification<Product> tourEndDateBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            Join<Product, Tour> tour = root.join("tour", JoinType.LEFT);
            if (from != null && to != null) {
                return builder.between(tour.get("endDate"), from, to);
            } else if (from != null) {
                return builder.greaterThanOrEqualTo(tour.get("endDate"), from);
            } else if (to != null) {
                return builder.lessThanOrEqualTo(tour.get("endDate"), to);
            }
            return null;
        };
    }

}
