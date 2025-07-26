// 검색 결과중 코드 | 영문 | 한글 중 "코드"만 남기는 함수
function stripAfterPipe(input) {
    const val = input.value;
    if (val.includes('|')) {
        input.value = val.split('|')[0].trim();
    }
}