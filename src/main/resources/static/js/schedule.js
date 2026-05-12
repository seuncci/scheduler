let currentYear, currentMonth, currentDay;

function initCalendar() {
    const urlParams = new URLSearchParams(window.location.search);
    const now = new Date();

    currentYear = parseInt(urlParams.get('year')) || now.getFullYear();
    currentMonth = parseInt(urlParams.get('month')) || (now.getMonth() + 1);
    currentDay = parseInt(urlParams.get('day')) || now.getDate();

    const currentView = urlParams.get('view') || 'monthly';
    changeTab(currentView, true);
}

window.addEventListener('DOMContentLoaded', initCalendar);

/*
const monthlyHTML = `
  <div class="min-w-[600px] animate-in fade-in duration-300">
      <div class="grid grid-cols-7 mb-4 border-b border-gray-50 pb-3">
        <div class="text-center text-[12px] font-black text-red-400 uppercase tracking-widest">일</div>
        <div class="text-center text-[12px] font-black text-gray-400 uppercase tracking-widest">월</div>
        <div class="text-center text-[12px] font-black text-gray-400 uppercase tracking-widest">화</div>
        <div class="text-center text-[12px] font-black text-gray-400 uppercase tracking-widest">수</div>
        <div class="text-center text-[12px] font-black text-gray-400 uppercase tracking-widest">목</div>
        <div class="text-center text-[12px] font-black text-gray-400 uppercase tracking-widest">금</div>
        <div class="text-center text-[12px] font-black text-blue-400 uppercase tracking-widest">토</div>
      </div>

      <div class="grid grid-cols-7 gap-px bg-gray-100 border border-gray-100 rounded-2xl overflow-hidden shadow-inner">
        <div class="bg-gray-50/50 aspect-square"></div>
        <div class="bg-gray-50/50 aspect-square"></div>
        <div class="bg-gray-50/50 aspect-square"></div>

        ${Array.from({length: 31}, (_, i) => {
          const day = i + 1;

          const isBusyDay = day === 15; // 일정이 많은 날
          const isRangeDay = day >= 20 && day <= 22; // 기간형 일정
          const isDeadlineDay = day === 24; // 마감형 일정

          const hasEvent = isBusyDay || isRangeDay || isDeadlineDay;

          return `

          <div class="${hasEvent ? 'bg-indigo-50/60' : 'bg-white'} aspect-square p-2 hover:bg-indigo-100/40 relative group cursor-pointer ${!hasEvent ? 'flex items-center justify-center' : ''}">

            <span class="absolute top-2 left-2.5 text-[11px] font-bold
              ${[5, 12, 19, 26].includes(day) ? 'text-red-400' : [4, 11, 18, 25, 31].includes(day) ? 'text-blue-400' : 'text-gray-400'}">${day}</span>

            <div class="hidden lg:block mt-6 space-y-1 w-full">

              ${isBusyDay ? `
                <div class="px-2 py-1 bg-indigo-500 text-white text-[9px] font-bold rounded-md truncate shadow-sm">10:00 팀 스크럼</div>
                <div class="px-2 py-1 bg-indigo-400 text-white text-[9px] font-bold rounded-md truncate shadow-sm">13:00 프로젝트 회의</div>
                <div class="px-2 py-0.5 bg-gray-200/80 text-gray-600 text-[8px] font-black rounded-md flex items-center justify-center border border-gray-300/50 mt-1">
                  + 2개 더보기
                </div>
              ` : ''}

              ${isRangeDay ? `
                <div class="px-2 py-1 bg-indigo-500 text-white text-[9px] font-bold rounded-md truncate shadow-sm">제주 워크숍</div>
              ` : ''}

              ${isDeadlineDay ? `
                <div class="px-2 py-1 bg-rose-500 text-white text-[9px] font-bold rounded-md truncate shadow-sm flex items-center gap-1">
                  <i class="fa-solid fa-flag text-[7px]"></i>
                  <span>최종 마감</span>
                </div>
              ` : ''}

            </div>
          </div>`;
        }).join('')}
      </div>
  </div>`;
*/

function renderMonthlyView(year, month, events = []) {
    const view = document.getElementById('calendar-view');
    const firstDay = new Date(year, month - 1, 1).getDay();
    const lastDate = new Date(year, month, 0).getDate();

    view.classList.remove('weekly-active');

    const eventMap = {};
    events.forEach(event => {

        let current = new Date(event.startDateTime || event.endDateTime);
        const end = new Date(event.endDateTime);

        const tempDate = new Date(current.getFullYear(), current.getMonth(), current.getDate());
        const tempEndDate = new Date(end.getFullYear(), end.getMonth(), end.getDate());

        while (tempDate <= tempEndDate) {
            const y = tempDate.getFullYear();
            const m = String(tempDate.getMonth() + 1).padStart(2, '0');
            const d = String(tempDate.getDate()).padStart(2, '0');
            const dateKey = `${y}-${m}-${d}`;

            if (!eventMap[dateKey]) eventMap[dateKey] = [];
            eventMap[dateKey].push(event);

            tempDate.setDate(tempDate.getDate() + 1);
        }
    });

    let html = `
    <div class="min-w-[600px] animate-in fade-in duration-300">
        <div class="grid grid-cols-7 mb-4 border-b border-gray-50 pb-3">
            <div class="text-center text-[12px] font-black text-red-400 uppercase tracking-widest">일</div>
            <div class="text-center text-[12px] font-black text-gray-400 uppercase tracking-widest">월</div>
            <div class="text-center text-[12px] font-black text-gray-400 uppercase tracking-widest">화</div>
            <div class="text-center text-[12px] font-black text-gray-400 uppercase tracking-widest">수</div>
            <div class="text-center text-[12px] font-black text-gray-400 uppercase tracking-widest">목</div>
            <div class="text-center text-[12px] font-black text-gray-400 uppercase tracking-widest">금</div>
            <div class="text-center text-[12px] font-black text-blue-400 uppercase tracking-widest">토</div>
        </div>
        <div class="grid grid-cols-7 gap-px bg-gray-100 border border-gray-100 rounded-2xl overflow-hidden shadow-inner">
    `;

    for (let i = 0; i < firstDay; i++) {
        html += `<div class="bg-gray-50/50 aspect-square"></div>`;
    }

    for (let day = 1; day <= lastDate; day++) {
        const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        const dayEvents = eventMap[dateStr] || [];
        const hasEvent = dayEvents.length > 0;

        const dayOfWeek = new Date(year, month - 1, day).getDay();
        const colorClass = dayOfWeek === 0 ? 'text-red-400' : dayOfWeek === 6 ? 'text-blue-400' : 'text-gray-400';

        html += `
        <div class="${hasEvent ? 'bg-indigo-50/30' : 'bg-white'} aspect-square p-1 md:p-2 hover:bg-indigo-100/40 relative group cursor-pointer border-r border-b border-gray-50">
            <span class="absolute top-2 left-2.5 text-[11px] font-bold ${colorClass}">${day}</span>

            <div class="hidden lg:block mt-6 space-y-1 w-full">
                ${dayEvents.slice(0, 2).map(event => {
                    const timeStr = formatTime(event.startDateTime, event.endDateTime);
                    return `
                    <div class="px-2 py-1 ${event.color || 'bg-indigo-500'} text-white text-[9px] font-bold rounded-md truncate shadow-sm">
                        ${timeStr ? `${timeStr} ` : ''}${event.title}
                    </div>`;
                }).join('')}

                ${dayEvents.length > 2 ? `
                <div class="px-2 py-0.5 bg-gray-200/80 text-gray-600 text-[8px] font-black rounded-md flex items-center justify-center border border-gray-300/50 mt-1">
                  + ${dayEvents.length - 2}개 더보기
                </div>` : ''}
            </div>

            <div class="lg:hidden absolute inset-0 ${hasEvent ? 'bg-indigo-500/10' : ''} pointer-events-none"></div>
        </div>`;
    }

    html += `</div></div>`;
    view.innerHTML = html;

    document.querySelector('h2').innerText = `${year}년 ${month}월`;
}

function formatTime(start, end) {
    if (!start || !end) return '';
    const s = new Date(start);
    const e = new Date(end);

    if (s.toDateString() === e.toDateString()) {
        const h = String(s.getHours()).padStart(2, '0');
        const m = String(s.getMinutes()).padStart(2, '0');

        return `${h}:${m}`;
    }
    return '';
}

async function fetchMonthlySchedules() {
    const startDate = new Date(currentYear, currentMonth - 1, 1).toISOString().split('T')[0];
    const endDate = new Date(currentYear, currentMonth, 0).toISOString().split('T')[0];

    try {
        const response = await fetch(`/api/schedules?startDate=${startDate}&endDate=${endDate}`);
        const result = await response.json();
        if (response.ok) {
            renderMonthlyView(currentYear, currentMonth, result.data);
        }
    } catch (e) {
        console.error("일정 로드 실패", e);
    }
}
/*
const weeklyHTML = `
  <div class="min-w-[700px] animate-in fade-in slide-in-from-bottom-2 duration-500 flex flex-col h-full">

    <div class="grid grid-cols-[60px_1fr_1fr_1fr_1fr_1fr_1fr_1fr] bg-white border-t border-x border-gray-100 rounded-t-[2rem] text-center py-4">
      <div class="text-[10px] font-black text-gray-300 flex items-end justify-center pb-1">GMT+9</div>
      <div class="flex flex-col items-center"><span class="text-[10px] font-black text-red-400">일</span><span class="text-lg font-black text-gray-900">19</span></div>
      <div class="flex flex-col items-center"><span class="text-[10px] font-black text-gray-400">월</span><span class="text-lg font-black text-gray-900">20</span></div>
      <div class="flex flex-col items-center"><span class="text-[10px] font-black text-gray-400">화</span><span class="text-lg font-black text-gray-900">21</span></div>
      <div class="flex flex-col items-center ring-2 ring-indigo-500 ring-offset-4 rounded-xl pb-1 bg-indigo-50/50">
        <span class="text-[10px] font-black text-indigo-600">수</span><span class="text-lg font-black text-indigo-600">22</span>
      </div>
      <div class="flex flex-col items-center"><span class="text-[10px] font-black text-gray-400">목</span><span class="text-lg font-black text-gray-900">23</span></div>
      <div class="flex flex-col items-center"><span class="text-[10px] font-black text-gray-400">금</span><span class="text-lg font-black text-gray-900">24</span></div>
      <div class="flex flex-col items-center"><span class="text-[10px] font-black text-blue-400">토</span><span class="text-lg font-black text-gray-900">25</span></div>
    </div>

    <div class="grid grid-cols-[60px_1fr_1fr_1fr_1fr_1fr_1fr_1fr] bg-white border-x border-b border-gray-50 text-[10px]">
      <div class="h-10 border-r border-gray-100 flex items-center justify-center text-gray-300 font-bold uppercase tracking-tighter">종일</div>
      <div class="col-start-3 col-end-6 py-2 px-1">
        <div class="h-full bg-indigo-500 text-white rounded-full px-4 flex items-center font-black shadow-sm">
          🚢 제주도 워크숍 (5/20 ~ 5/22)
        </div>
      </div>
    </div>

    <div id="weekly-scroll-container" class="relative grid grid-cols-[60px_1fr_1fr_1fr_1fr_1fr_1fr_1fr] bg-white border border-gray-100 rounded-b-[2rem] overflow-y-auto h-[600px] shadow-inner scroll-smooth">

      <div class="bg-gray-50/50 border-r border-gray-100 flex flex-col">
        ${Array.from({length: 24}, (_, i) => `
          <div id="time-${i}" class="h-24 border-b border-gray-100/30 flex items-start justify-center pt-2 text-[10px] font-bold text-gray-400 uppercase">
            ${i < 12 ? '오전' : '오후'} ${String(i === 0 || i === 12 ? 12 : i % 12).padStart(2, '0')}
          </div>
        `).join('')}
      </div>

      <div class="relative border-r border-gray-50 h-full"></div><div class="relative border-r border-gray-50 h-full"></div><div class="relative border-r border-gray-50 h-full"></div>

      <div class="relative border-r border-indigo-100 bg-indigo-50/10 h-full">
        <div class="absolute top-[960px] left-1 right-1 group cursor-pointer">
          <div class="p-3 bg-indigo-600 text-white rounded-2xl shadow-lg shadow-indigo-200 transition-transform group-hover:scale-[1.02] z-10">
            <p class="text-[10px] font-black opacity-80 uppercase">10:00 - 11:30</p>
            <p class="text-[12px] font-black mt-0.5 truncate">주간 팀 스크럼 회의</p>
          </div>
          <div class="mt-1.5 ml-1 flex items-center gap-1.5">
            <div class="px-2 py-0.5 bg-white border border-indigo-100 rounded-full shadow-sm"><p class="text-[9px] font-bold text-indigo-600">+ 2개 더보기</p></div>
          </div>
        </div>

        <div class="absolute top-[1440px] left-1 right-1 p-2.5 bg-white border-2 border-rose-500 rounded-xl shadow-md z-10 transition-transform hover:scale-[1.02] cursor-pointer">
           <div class="flex items-center gap-1.5">
             <div class="w-2 h-2 bg-rose-500 rounded-full animate-pulse"></div>
             <div>
               <p class="text-[11px] font-black text-gray-800 leading-none">기획안 최종 마감</p>
               <p class="text-[9px] font-bold text-rose-500 mt-1 uppercase">~ 오후 03:00</p>
             </div>
           </div>
        </div>
      </div>

      <div class="relative border-r border-gray-50 h-full"></div><div class="relative border-r border-gray-50 h-full"></div><div class="relative h-full"></div>

      <div class="absolute inset-0 pointer-events-none">
        ${Array.from({length: 24}, () => `<div class="h-24 border-b border-gray-100/50 w-full"></div>`).join('')}
      </div>
    </div>
  </div>
`;
*/

function renderWeeklyView(year, month, events = []) {
    const view = document.getElementById('calendar-view');
    const baseDate = new Date(year, month - 1, currentDay);

    view.classList.add('weekly-active');

    const startOfWeek = new Date(baseDate);
    startOfWeek.setDate(baseDate.getDate() - baseDate.getDay());
    startOfWeek.setHours(0, 0, 0, 0);

    const endOfWeek = new Date(startOfWeek);
    endOfWeek.setDate(startOfWeek.getDate() + 6);
    endOfWeek.setHours(23, 59, 59, 999);

    const days = Array.from({ length: 7 }, (_, i) => {
        const d = new Date(startOfWeek);
        d.setDate(startOfWeek.getDate() + i);
        return d;
    });

    const allDayEvents = [];
    const timeMap = {};
    const processedAllDayIds = new Set();

    events.forEach(event => {
        const start = event.startDateTime ? new Date(event.startDateTime) : null;
        const end = new Date(event.endDateTime);

        const isAllDay = (!start && end.getHours() === 0 && end.getMinutes() === 0) ||
                         (start && start.toDateString() !== end.toDateString());

        if (isAllDay) {
            if (processedAllDayIds.has(event.id)) return;

            const eventStart = start || end;
            if (eventStart <= endOfWeek && end >= startOfWeek) {
                allDayEvents.push(event);
                processedAllDayIds.add(event.id);
            }
        } else {
            const dKey = formatDate(start || end);
            const tKey = `${dKey}-${(start || end).getHours()}`;
            if (!timeMap[tKey]) timeMap[tKey] = [];
            timeMap[tKey].push(event);
        }
    });

    const layers = [new Array(7).fill(false)];
    const sortedAllDay = allDayEvents.sort((a, b) => new Date(a.startDateTime || a.endDateTime) - new Date(b.startDateTime || b.endDateTime));

    const renderData = [];
    const moreCounts = new Array(7).fill(0);
    let totalMoreCount = 0;
    let lastEndCol = 0;

    sortedAllDay.forEach(e => {
        const s = new Date(e.startDateTime || e.endDateTime);
        const ed = new Date(e.endDateTime);

        let startCol = Math.max(0, Math.floor((s - startOfWeek) / (1000 * 60 * 60 * 24)));
        let endCol = Math.min(6, Math.floor((ed - startOfWeek) / (1000 * 60 * 60 * 24)));

        let isFree = true;
        for (let c = startCol; c <= endCol; c++) {
            if (layers[0][c]) { isFree = false; break; }
        }

        if (isFree) {

            for (let c = startCol; c <= endCol; c++) layers[0][c] = true;
            renderData.push({ event: e, startCol, span: endCol - startCol + 1, layer: 0 });
        } else {
            totalMoreCount++;
            lastEndCol = Math.max(lastEndCol, endCol);
            // for (let c = startCol; c <= endCol; c++) moreCounts[c]++;
        }
    });

    let html = `
    <div class="min-w-[700px] animate-in fade-in slide-in-from-bottom-2 duration-500 flex flex-col h-full">
        <div class="grid grid-cols-[60px_1fr_1fr_1fr_1fr_1fr_1fr_1fr] bg-white border-t border-x border-gray-100 rounded-t-[2rem] text-center py-4">
            <div class="text-[10px] font-black text-gray-300 flex items-end justify-center pb-1">GMT+9</div>
            ${days.map(d => {
                const isToday = d.toDateString() === new Date().toDateString();
                const dayLabel = ['일','월','화','수','목','금','토'][d.getDay()];
                const color = d.getDay() === 0 ? 'text-red-500' : d.getDay() === 6 ? 'text-blue-500' : 'text-gray-500';
                return `<div class="flex flex-col items-center ${isToday ? 'ring-2 ring-indigo-500 ring-offset-4 rounded-xl pb-1 bg-indigo-50/50' : ''}">
                    <span class="text-[10px] font-black ${isToday ? 'text-indigo-600' : color}">${dayLabel}</span>
                    <span class="text-xl font-black ${isToday ? 'text-indigo-600' : 'text-gray-900'}">${d.getDate()}</span>
                </div>`;
            }).join('')}
        </div>

        <div class="grid grid-cols-[60px_1fr_1fr_1fr_1fr_1fr_1fr_1fr] bg-white border-x border-b border-gray-50 text-[10px] relative min-h-[50px]">
            <div class="border-r border-gray-100 flex items-center justify-center text-gray-400 font-black uppercase tracking-tighter shrink-0 z-20 bg-white">종일</div>
            ${days.map(() => `<div class="border-r border-gray-60 min-h-[60px]"></div>`).join('')}

            <div class="absolute inset-0 left-[60px] pointer-events-none p-1">
                <div class="relative w-full h-full">
                    ${renderData.map(d => {
                        const s = new Date(d.event.startDateTime || d.event.endDateTime);
                        const ed = new Date(d.event.endDateTime);

                        const dateRange = `(${s.getMonth() + 1}/${s.getDate()} ~ ${ed.getMonth() + 1}/${ed.getDate()})`;

                        return `
                        <div class="absolute pointer-events-auto transition-all"
                             style="top: 8px; left: ${(d.startCol * 14.28)}%; width: ${(d.span * 14.28)}%; padding: 2px; z-index: 10;">
                            <div class="h-8 ${d.event.color || 'bg-indigo-500'} text-white rounded-full px-4 flex items-center font-black shadow-sm truncate border border-white/20 text-[10px]">
                               ${d.event.title} ${dateRange}
                            </div>
                        </div>`;
                    }).join('')}

                    ${totalMoreCount > 0 ? `
                        <div class="absolute pointer-events-auto"
                             style="top: 36px; left: ${(lastEndCol * 14.28)}%; width: 14.28%; display: flex; justify-content: center; z-index: 10;">
                            <div class="px-3 py-1 bg-indigo-50 border border-indigo-100 rounded-full shadow-sm">
                                <p class="text-[10px] font-black text-indigo-600">+ ${totalMoreCount}개 더보기</p>
                            </div>
                        </div>
                    ` : ''}
                </div>
            </div>
        </div>

        <div id="weekly-scroll-container" class="relative grid grid-cols-[60px_1fr_1fr_1fr_1fr_1fr_1fr_1fr] bg-white border border-gray-100 rounded-b-[2rem] overflow-y-auto h-[600px] shadow-inner scroll-smooth">
            <div class="bg-gray-50/50 border-r border-gray-100 flex flex-col">
                ${Array.from({length: 24}, (_, i) => `<div id="time-${i}" class="h-24 border-b border-gray-100/30 flex items-start justify-center pt-2 text-[10px] font-bold text-gray-400 uppercase">${i < 12 ? '오전' : '오후'} ${String(i % 12 || 12).padStart(2, '0')}</div>`).join('')}
            </div>
            ${days.map(d => {
                const dKey = formatDate(d);
                return `<div class="relative border-r border-gray-50 h-full">
                    ${Array.from({length: 24}, (_, h) => {
                        const cellEvents = timeMap[`${dKey}-${h}`] || [];
                        if (cellEvents.length === 0) return `<div class="h-24 border-b border-gray-100/50"></div>`;
                        const e = cellEvents[0];
                        const isDeadline = !e.startDateTime;
                        return `
                        <div class="h-24 border-b border-gray-100/50 p-2 relative">
                            ${isDeadline ? `
                            <div class="p-2 bg-white border-2 border-rose-500 rounded-xl shadow-md cursor-pointer h-full flex flex-col justify-center overflow-hidden">
                               <div class="flex items-center gap-1.5 w-full">
                                 <div class="w-2 h-2 bg-rose-500 rounded-full animate-pulse shrink-0"></div>
                                 <div class="min-w-0 flex-1">
                                   <p class="text-[11px] font-black text-gray-800 leading-tight truncate">${e.title}</p>
                                   <p class="text-[9px] font-bold text-rose-500 mt-0.5 uppercase truncate">~ ${formatTimeOnly(e.endDateTime)}</p>
                                 </div>
                               </div>
                            </div>
                        ` : `
                            <div class="p-3 ${e.color || 'bg-indigo-600'} text-white rounded-2xl shadow-lg transition-transform hover:scale-[1.02] cursor-pointer h-full overflow-hidden">
                                <p class="text-[10px] font-black opacity-80 uppercase truncate">${formatRange(e.startDateTime, e.endDateTime)}</p>
                                <p class="text-[12px] font-black mt-0.5 truncate">${e.title}</p>
                            </div>
                            `}
                        </div>`;
                    }).join('')}
                </div>`;
            }).join('')}
        </div>
    </div>`;

    view.innerHTML = html;

    // 제목 업데이트 (명시적 ID 사용 권장)
    const headerTitle = document.querySelector('header h2') || document.querySelector('h2');
    if (headerTitle) headerTitle.innerText = `${year}년 ${month}월`;
}

function formatDate(date) { return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`; }

function formatRange(start, end) {
    const s = new Date(start);
    const e = new Date(end);
    return `${String(s.getHours()).padStart(2, '0')}:${String(s.getMinutes()).padStart(2, '0')} - ${String(e.getHours()).padStart(2, '0')}:${String(e.getMinutes()).padStart(2, '0')}`;
}

function formatTimeOnly(dateStr) {
    const d = new Date(dateStr);
    const h = d.getHours();
    const m = String(d.getMinutes()).padStart(2, '0');
    const ampm = h < 12 ? '오전' : '오후';
    const displayH = String(h % 12 || 12).padStart(2, '0');
    return `${ampm} ${displayH}:${m}`;
}

async function fetchWeeklySchedules() {

    const curr = new Date(currentYear, currentMonth - 1, currentDay);
    const first = curr.getDate() - curr.getDay();
    const last = first + 6;

    const startDate = new Date(currentYear, currentMonth - 1, 1).toISOString().split('T')[0];
    const endDate = new Date(currentYear, currentMonth, 0).toISOString().split('T')[0];

    try {
        const response = await fetch(`/api/schedules?startDate=${startDate}&endDate=${endDate}`);
        const result = await response.json();
        if (response.ok) {
            renderWeeklyView(currentYear, currentMonth, result.data);
            setTimeout(scrollToNine, 50);
        }
    } catch (e) { console.error("주간 일정 로드 실패", e); }
}

const view = document.getElementById('calendar-view');
  const bM = document.getElementById('btn-monthly');
  const bW = document.getElementById('btn-weekly');

  function changeTab(type, isFirstLoad = false) {

    if (type === 'weekly') {

      fetchWeeklySchedules();
      setTimeout(scrollToNine, 50);
      bW.className = "px-4 py-1.5 text-[11px] font-bold rounded-lg bg-white shadow-sm text-indigo-600 transition-all";
      bM.className = "px-4 py-1.5 text-[11px] font-bold text-gray-400 hover:text-gray-600 transition-all";
    } else {
      fetchMonthlySchedules();
      bM.className = "px-4 py-1.5 text-[11px] font-bold rounded-lg bg-white shadow-sm text-indigo-600 transition-all";
      bW.className = "px-4 py-1.5 text-[11px] font-bold text-gray-400 hover:text-gray-600 transition-all";
    }

    if (!isFirstLoad) {
      const newUrl = `${window.location.pathname}?view=${type}&year=${currentYear}&month=${currentMonth}&day=${currentDay}`;
      window.history.pushState({ v: type }, '', newUrl);
    }
  }

  function updateURL() {
      const params = new URLSearchParams(window.location.search);
      params.set('year', currentYear);
      params.set('month', currentMonth);
      params.set('day', currentDay);

      const isWeekly = bW.classList.contains('text-indigo-600');
      const viewType = isWeekly ? 'weekly' : 'monthly';
      params.set('view', viewType);

      const newUrl = `${window.location.pathname}?${params.toString()}`;
      window.history.pushState({ year: currentYear, month: currentMonth, day: currentDay, v: viewType }, '', newUrl);
  }

  document.querySelectorAll('.flex.items-center.bg-gray-50 button').forEach((btn, idx) => {
      btn.onclick = () => {

          const isWeekly = bW.classList.contains('text-indigo-600');
          const date = new Date(currentYear, currentMonth - 1, currentDay);

          if (idx === 0) {
              if (isWeekly) date.setDate(date.getDate() - 7); // 7일 전
              else {
                date.setMonth(date.getMonth() - 1);
                date.setDate(1);
              }
          } else if (idx === 1) {
              const now = new Date();
              date.setFullYear(now.getFullYear());
              date.setMonth(now.getMonth());
              date.setDate(now.getDate());
          } else if (idx === 2) {
              if (isWeekly) date.setDate(date.getDate() + 7); // 7일 후
              else {
                date.setMonth(date.getMonth() + 1);
                date.setDate(1);
              }
          }

          currentYear = date.getFullYear();
          currentMonth = date.getMonth() + 1;
          currentDay = date.getDate();

          updateURL();
          if (isWeekly) fetchWeeklySchedules();
          else fetchMonthlySchedules();
      };
  });

  bM.onclick = function() { changeTab('monthly'); };
  bW.onclick = function() { changeTab('weekly'); };

  window.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const currentView = urlParams.get('view');

    if (currentView === 'weekly') {
      changeTab('weekly', true);
    } else {
      changeTab('monthly', true);
    }
  });

  window.onpopstate = function(event) {
    if (event.state && event.state.v) {
      changeTab(event.state.v, true);
    } else {
      changeTab('monthly', true);
    }
  };

  function scrollToNine() {
      const container = document.getElementById('weekly-scroll-container');
      const nineAM = document.getElementById('time-9');
      if (container && nineAM) {

          container.scrollTop = nineAM.offsetTop;
      }
  }

  async function toggleGroupSelect(value) {
    const container = document.getElementById('group-select-container');
    const groupInput = document.getElementById('groupId');

    if (value === 'GROUP') {

      await loadMyGroups();

      container.classList.remove('hidden');
      groupInput.required = true;
    } else {
      container.classList.add('hidden');
      groupInput.required = false;
    }
  }

  function openAddEventModal() {
    const modal = document.getElementById('event-modal');

    modal.classList.remove('hidden');
    modal.classList.add('flex');

    document.body.style.overflow = 'hidden';
  }

  function closeAddEventModal() {
    const modal = document.getElementById('event-modal');
    const groupContainer = document.getElementById('group-select-container');

    if (modal) {
      modal.classList.replace('flex', 'hidden');
      document.body.style.overflow = 'auto';

      document.getElementById('event-form').reset();


      if (groupContainer) {
        groupContainer.classList.add('hidden');
      }
    }
  }

  async function loadMyGroups() {
      const groupSelect = document.getElementById('groupId');

      try {
          const response = await fetch('/api/groups/mine');
          const result = await response.json();

          if (response.ok) {

              groupSelect.innerHTML = '<option value="">그룹을 선택해주세요</option>';

              result.data.forEach(group => {
                  const option = document.createElement('option');
                  option.value = group.id;
                  option.textContent = `${group.name}`;
                  groupSelect.appendChild(option);
              });
          } else {

              showToast('error', result.message);

              setTimeout(() => {
                document.getElementById('group-select-container').classList.add('hidden');
                document.getElementById('groupId').required = false;
                document.getElementById('targetType').value = 'PRIVATE';
              }, 10);
          }
      } catch (error) {
          console.error('그룹 로딩 실패:', error);
          showToast('error', '서버와의 통신이 원활하지 않습니다.');
      }
  }

  document.getElementById('event-form').onsubmit = async function(e) {
      e.preventDefault();

      const formData = new FormData(this);
      const requestData = Object.fromEntries(formData.entries());

      requestData.targetType = document.getElementById('targetType').value;

      const groupIdValue = document.getElementById('groupId').value;
      requestData.groupId = (requestData.targetType === 'GROUP' && groupIdValue)
                            ? parseInt(groupIdValue) : null;

      const combineDateTime = (dateId, timeId) => {
          const dateValue = document.getElementById(dateId).value;
          const timeValue = document.getElementById(timeId).value;

          if (!dateValue) return null;

          return timeValue ? `${dateValue}T${timeValue}` : dateValue;
      };

  requestData.startDateTime = combineDateTime('startDate', 'startTime');
  requestData.endDateTime = combineDateTime('endDate', 'endTime');

      if (!requestData.title.trim()) {
          showToast('error', '일정명을 입력해주세요.');
          return;
      }
      if (!requestData.endDateTime) {
          showToast('error', '종료 날짜를 선택해주세요.');
          return;
      }

      try {
          const response = await fetch('/api/schedules', {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify(requestData)
          });

          const result = await response.json();

          if (response.ok) {
              showToast('success', result.message);
              setTimeout(() => {
                  location.reload();
              }, 2000);
          } else {
              showToast('error', result.message);
          }
      } catch (error) {
          console.error('Save Error:', error);
          showToast('error', '서버와의 통신에 실패했습니다.');
      }
  };