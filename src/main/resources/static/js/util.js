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


/**
 * 이미지를 Google Drive에 업로드하는 함수
 * @param {HTMLElement} inputElement - 파일 입력 요소
 * @param {string} folderType - 업로드할 폴더 타입 (product, notice, banner, popup, promotion, hotel, excel)
 */
function uploadImage(inputElement, folderType) {
    const file = inputElement.files[0];
    if (!file) return;

    // folderType이 지정되지 않으면 기본값 사용
    // inputElement의 data-folder-type 속성 또는 부모 요소에서 찾기
    if (!folderType) {
        folderType = inputElement.getAttribute('data-folder-type') || 
                     inputElement.closest('[data-folder-type]')?.getAttribute('data-folder-type') ||
                     'product';  // 기본값: product
    }

    const formData = new FormData();
    formData.append("file", file);
    formData.append("folderType", folderType);

    // 업로드 중 표시
    const originalValue = inputElement.value;
    inputElement.disabled = true;
    const imageInput = inputElement.previousElementSibling;  // input[type="text"]
    if (imageInput) {
        imageInput.value = "업로드 중...";
        imageInput.disabled = true;
    }

    fetch("/api/files/upload", {  // Google Drive 업로드 엔드포인트
        method: "POST",
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("업로드 실패: " + response.status);
            }
            return response.json();
        })
        .then(data => {
            console.log("업로드 성공:", data);
            // 이미지 직접 표시용 URL을 input 필드에 채우기 (클라이언트에서 <img> 태그로 사용)
            if (imageInput) {
                // imageUrl이 있으면 사용, 없으면 driveLink 사용
                imageInput.value = data.imageUrl || data.driveLink || data.filename || "";
                imageInput.disabled = false;
            }
        })
        .catch(error => {
            console.error("이미지 업로드 실패:", error);
            alert("이미지 업로드 실패: " + error.message);
            if (imageInput) {
                imageInput.value = "";
                imageInput.disabled = false;
            }
        })
        .finally(() => {
            inputElement.disabled = false;
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