// 검색 결과중 코드 | 영문 | 한글 중 "코드"만 남기는 함수
function stripAfterPipe(input) {
    const val = input.value;
    if (val.includes('|')) {
        input.value = val.split('|')[0].trim();
    }
}

// 선택 팝업에서 특정 튜플 선택시 부모창에 전달하고 팝업 닫음
function returnNumber(id) {
    console.log('returnNumber' + id)
    if (window.opener && !window.opener.closed) {
        window.opener.setId(id);  // 부모 setId 함수 호출
        window.close(); // 팝업 닫기
    } else {
        console.log('returnNumber 오류')
    }
}

// 선택 팝업에서 특정 튜플 선택시 부모창에 가격정보 전달하고 팝업 닫음
function returnPrice(id, tourPrice, airPrice, hotelPrice) {
    console.log(`returnPrice : ${id}, ${tourPrice}, ${airPrice}, ${hotelPrice}, `)
    if (window.opener && !window.opener.closed) {
        window.opener.setPrice(id, tourPrice, airPrice, hotelPrice);  // 부모 setPrice 함수 호출
        window.close(); // 팝업 닫기
    } else {
        console.log('returnPrice 오류')
    }
}