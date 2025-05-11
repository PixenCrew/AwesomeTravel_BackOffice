function get(page, event, sortField = null, sortDir = null) {
    if (event) {
        event.preventDefault(); // form+button 기본 동작 방지
    }
    const form = document.getElementById('filterForm');
    const formData = new FormData(form);
    const params = new URLSearchParams();

    for (const [key, value] of formData.entries()) {
        if (value != null && value !== '' && !key.startsWith('_')) {
            params.append(key, value);
        }
    }

    // 페이지 파라미터
    if (page != null) {
        params.set('page', page);
    }

    // console.log("params:"+params.getAll())
    const baseUrl = location.pathname;
    const qs = params.toString();
    const url = baseUrl + (qs ? `?${qs}` : '');

    // if (confirm("GET?")) {
    window.location.href = url;
    // }
}

function reset(event) {
    console.log("sadasd")
    event.preventDefault();

    if (confirm("필터를 초기화합니다.")) {
        // form 선택
        const form = document.getElementById("filterForm");

        // form 항목 모두 빈값으로 초기화
        form.reset();

        // 기본 페이지로 이동
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

    get(0, event, field, nextDir);
}