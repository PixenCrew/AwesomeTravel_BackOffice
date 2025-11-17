package renewal.awesome_travel_backoffice.review.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BannedWordFilterService {

    private Set<String> bannedWords = new HashSet<>();
    private static final String REPLACEMENT = "***";

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("banned-words.txt");
            try (InputStream inputStream = resource.getInputStream();
                 BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        bannedWords.add(line.toLowerCase());
                    }
                }
                log.info("금지어 {}개를 로드했습니다.", bannedWords.size());
            }
        } catch (IOException e) {
            log.error("금지어 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 텍스트에서 금지어를 필터링하여 ***로 대체합니다.
     * 문자 사이에 숫자나 특수문자를 끼워넣은 경우도 감지합니다.
     * @param text 원본 텍스트
     * @return 필터링된 텍스트
     */
    public String filter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String filtered = text;
        String lowerText = text.toLowerCase();

        // 각 금지어를 찾아서 대체
        for (String bannedWord : bannedWords) {
            if (bannedWord.length() < 2) {
                // 1글자 금지어는 일반 매칭
                if (lowerText.contains(bannedWord)) {
                    Pattern pattern = Pattern.compile(
                        Pattern.quote(bannedWord), 
                        Pattern.CASE_INSENSITIVE
                    );
                    filtered = pattern.matcher(filtered).replaceAll(REPLACEMENT);
                }
            } else {
                // 2글자 이상: 문자 사이에 특수문자/숫자/공백이 들어간 경우도 감지
                // 예: "병신" -> "병1신", "병@신", "병  신" 등도 감지
                String regexPattern = buildFlexiblePattern(bannedWord);
                Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
                
                if (pattern.matcher(lowerText).find()) {
                    filtered = pattern.matcher(filtered).replaceAll(REPLACEMENT);
                }
            }
        }

        return filtered;
    }

    /**
     * 금지어를 유연한 정규식 패턴으로 변환합니다.
     * 각 문자 사이에 0개 이상의 특수문자, 숫자, 공백이 들어갈 수 있습니다.
     * 예: "병신" -> "병[\\W\\d\\s]*신"
     */
    private String buildFlexiblePattern(String word) {
        if (word.length() == 1) {
            return Pattern.quote(word);
        }
        
        StringBuilder pattern = new StringBuilder();
        char[] chars = word.toCharArray();
        
        for (int i = 0; i < chars.length; i++) {
            // 각 문자를 이스케이프 처리
            pattern.append(Pattern.quote(String.valueOf(chars[i])));
            
            // 마지막 문자가 아니면 사이에 특수문자/숫자/공백이 올 수 있음
            if (i < chars.length - 1) {
                // \\W: 특수문자, \\d: 숫자, \\s: 공백
                // +: 1개 이상, *: 0개 이상 (여기서는 0개 이상으로 설정하여 일반 케이스도 포함)
                pattern.append("[\\W\\d\\s]*");
            }
        }
        
        return pattern.toString();
    }

    /**
     * 텍스트에 금지어가 포함되어 있는지 확인합니다.
     * 문자 사이에 숫자나 특수문자를 끼워넣은 경우도 감지합니다.
     * @param text 확인할 텍스트
     * @return 금지어가 포함되어 있으면 true
     */
    public boolean containsBannedWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        String lowerText = text.toLowerCase();
        for (String bannedWord : bannedWords) {
            if (bannedWord.length() < 2) {
                if (lowerText.contains(bannedWord)) {
                    return true;
                }
            } else {
                String regexPattern = buildFlexiblePattern(bannedWord);
                Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
                if (pattern.matcher(lowerText).find()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 금지어 개수를 반환합니다.
     */
    public int getBannedWordCount() {
        return bannedWords.size();
    }
}

