// ✅ 전역 함수들 정의 - 모든 페이지에서 공통 사용
function toggleFilter() {
    const filterForm = document.getElementById('filterForm');
    const toggleBtn = document.getElementById('toggleFilterBtn');
    
    if (!filterForm || !toggleBtn) {
        console.error('Filter elements not found');
        return;
    }
    
    if (filterForm.classList.contains('hidden')) {
        filterForm.classList.remove('hidden');
        toggleBtn.innerHTML = '<i class="fas fa-chevron-up mr-1"></i>접기';
    } else {
        filterForm.classList.add('hidden');
        toggleBtn.innerHTML = '<i class="fas fa-filter mr-1"></i>필터';
    }
}

// ✅ 통합된 검색 함수 (기존 get, performSearch 통합)
function performSearch(page = 0, event = null, sortField = null, sortDir = null) {
    if (event) {
        event.preventDefault();
    }
    
    const form = document.getElementById('filterForm');
    if (!form) {
        console.error('Filter form not found');
        return;
    }
    
    const formData = new FormData(form);
    
    // 현재 URL의 모든 쿼리 파라미터를 가져옴 (팝업 페이지의 isSelectionPage 등 유지)
    const currentParams = new URLSearchParams(window.location.search);
    const params = new URLSearchParams();

    // 먼저 현재 URL의 파라미터를 복사 (form 필드와 중복되지 않는 것들)
    for (const [key, value] of currentParams.entries()) {
        // form에 없는 필드들만 유지 (form 필드는 나중에 덮어씀)
        if (!formData.has(key) || key.startsWith('_')) {
            params.append(key, value);
        }
    }

    // form 필드 값 추가/덮어쓰기
    for (const [key, value] of formData.entries()) {
        if (value != null && value !== '' && !key.startsWith('_')) {
            params.set(key, value); // set으로 덮어쓰기
        }
    }

    // 페이지 파라미터
    if (page != null) {
        params.set('page', page);
    }

    // 정렬 파라미터 (toggleSort에서 사용)
    if (sortField && sortDir) {
        params.set('sortField', sortField);
        params.set('sortDir', sortDir);
    }

    const baseUrl = location.pathname;
    const qs = params.toString();
    const url = baseUrl + (qs ? `?${qs}` : '');
    
    console.log('Searching with URL:', url);
    window.location.href = url;
}

// ✅ 통합된 초기화 함수 (기존 resetFilter, resetFilterForm 통합)
function resetFilterForm(event = null) {
    if (event) {
        event.preventDefault();
    }
    
    if (confirm("필터를 초기화합니다.")) {
        const form = document.getElementById("filterForm");
        if (form) {
            form.reset();
        }
        const baseUrl = location.pathname;
        window.location.href = baseUrl;
    }
}

function toggleSort(el, event) {
    const field = el.dataset.sortfield;
    const currentDir = el.dataset.sortdir;
    console.log("currentDir" + currentDir)
    const nextDir = currentDir == 'asc' ? 'desc' : 'asc';
    console.log("nextDir" + nextDir)

    // form 내부 hidden 필드 업데이트
    const form = document.getElementById('filterForm');
    form.querySelector('input[name="sortField"]').value = field;
    form.querySelector('input[name="sortDir"]').value = nextDir;

    performSearch(0, event, field, nextDir);
}

// ✅ 전역(window)에 수동 등록 - onclick에서 접근 가능하도록
window.toggleFilter = toggleFilter;
window.performSearch = performSearch;  // 통합된 검색 함수
window.resetFilterForm = resetFilterForm;  // 통합된 초기화 함수
window.toggleSort = toggleSort;

// ✅ 별칭 제거 - 모든 파일에서 통일된 함수명 사용

// ✅ 로드 확인용 로그
console.log("filter.js loaded - toggleFilter available globally");