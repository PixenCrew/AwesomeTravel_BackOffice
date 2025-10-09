-- region_category 테이블 생성
CREATE TABLE IF NOT EXISTS region_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    country_code VARCHAR(255)
);

-- 테스트 데이터 삽입
INSERT INTO region_category (id, name, type, country_code) VALUES
(1, '서울', 'CITY', 'KR'),
(2, '부산', 'CITY', 'KR'),
(3, '제주도', 'CITY', 'KR'),
(4, '도쿄', 'CITY', 'JP'),
(5, '오사카', 'CITY', 'JP'),
(6, '베이징', 'CITY', 'CN'),
(7, '상하이', 'CITY', 'CN'),
(8, '뉴욕', 'CITY', 'US'),
(9, '로스앤젤레스', 'CITY', 'US'),
(10, '파리', 'CITY', 'FR'),
(11, '런던', 'CITY', 'GB'),
(12, '시드니', 'CITY', 'AU'),
(13, '동남아시아', 'REGION', 'SEA'),
(14, '유럽', 'REGION', 'EU'),
(15, '북미', 'REGION', 'NA');
