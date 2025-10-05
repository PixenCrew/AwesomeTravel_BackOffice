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

// 선택 팝업에서 특정 튜플 선택시 부모창에 정보 전달하고 팝업 닫음
function returnData(trElement) {
    const params = {
        id: Number(trElement.dataset.id),
        priceAdult: Number(trElement.dataset.priceAdult),
        hotelPriceSum: Number(trElement.dataset.hotelPrice),
        country: trElement.dataset.country,
        startDate: trElement.dataset.start,
        endDate: trElement.dataset.end
    };

    console.log('returnData:', params);

    if (window.opener && !window.opener.closed) {
        window.opener.setData(params);  // 부모 창 함수 호출
        window.close(); // 팝업 닫기
    } else {
        console.log('returnData 오류');
    }
}


function uploadImage(inputElement) {
    const file = inputElement.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("file", file);

    fetch("/image", {  // 이미지 업로드 엔드포인트
        method: "POST",
        body: formData
    })
        .then(response => response.text())
        .then(url => {
            console.log("url : " + url)
            // 서버에서 받은 URL을 해당 input 필드에 채우기
            const imageInput = inputElement.previousElementSibling;  // input[type="text"]
            imageInput.value = url;
        })
        .catch(error => {
            alert("이미지 업로드 실패: " + error.message);
        });
}

function removeImage(btn) {
    const item = btn.closest(".image-item");
    const imageUrlInput = item.querySelector('input[type="text"]');
    const imageUrl = imageUrlInput.value;

    if (imageUrl != null) {
        if (confirm('정말로 삭제하시겠습니까?')) {

            // 서버에 DELETE 요청
            fetch(`/image?target=${encodeURIComponent(imageUrl)}`, {
                method: 'DELETE'
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('서버에서 삭제 실패');
                    }
                    // 요소 제거
                    item.remove();
                })
                .catch(error => {
                    alert('이미지 삭제 실패: ' + error.message);
                });
        }
    }
}