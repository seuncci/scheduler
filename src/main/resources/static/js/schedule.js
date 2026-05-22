let currentYear, currentMonth, currentDay;
let currentDeleteCommentId = null;

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
        <div onclick="handleSideDayClick('${dateStr}')" class="${hasEvent ? 'bg-indigo-50/30' : 'bg-white'} aspect-square p-1 md:p-2 hover:bg-indigo-100/40 relative group cursor-pointer border-r border-b border-gray-50">
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

        if (response.status === 401 || response.redirected) {
            handleSessionTimeout();
            return;
        }

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

            renderData.push({ event: e, startCol, span: endCol - startCol + 1, endCol: endCol, layer: 0 });
        } else {
            totalMoreCount++;
        }
    });

    if (renderData.length > 0 && renderData[0].endCol !== undefined) {
        lastEndCol = renderData[0].endCol;
    }

    let html = `
    <div class="min-w-[700px] animate-in fade-in slide-in-from-bottom-2 duration-500 flex flex-col h-full">
        <div class="grid grid-cols-[60px_1fr_1fr_1fr_1fr_1fr_1fr_1fr] bg-white border-t border-x border-gray-100 rounded-t-[2rem] text-center py-4">
            <div class="text-[10px] font-black text-gray-300 flex items-end justify-center pb-1">GMT+9</div>
            ${days.map(d => {
                const isToday = d.toDateString() === new Date().toDateString();
                const dayLabel = ['일','월','화','수','목','금','토'][d.getDay()];
                const color = d.getDay() === 0 ? 'text-red-500' : d.getDay() === 6 ? 'text-blue-500' : 'text-gray-500';
                const todayClass = isToday
                                    ? 'outline outline-2 outline-indigo-500 outline-offset-[-2px] bg-indigo-50/60 rounded-2xl pb-1 px-1'
                                    : '';
                return `<div class="flex flex-col items-center justify-center min-h-[50px] ${todayClass}">
                    <span class="text-[10px] font-black ${isToday ? 'text-indigo-600' : color}">${dayLabel}</span>
                    <span class="text-xl font-black ${isToday ? 'text-indigo-600' : 'text-gray-900'}">${d.getDate()}</span>
                </div>`;
            }).join('')}
        </div>

        <div class="grid grid-cols-[60px_1fr_1fr_1fr_1fr_1fr_1fr_1fr] bg-white border-x border-b border-gray-50 text-[10px] relative min-h-[80px]">
            <div class="border-r border-gray-100 flex items-center justify-center text-gray-400 font-black uppercase tracking-tighter shrink-0 z-20 bg-white">종일</div>

            ${days.map(() => `<div class="border-r border-gray-50 min-h-[80px]"></div>`).join('')}

            <div class="absolute inset-0 left-[60px] pointer-events-none p-1">
                <div class="relative w-full h-full">
                    ${renderData.map(d => {
                        const s = new Date(d.event.startDateTime || d.event.endDateTime);
                        const ed = new Date(d.event.endDateTime);
                        const dateRange = `(${s.getMonth() + 1}/${s.getDate()} ~ ${ed.getMonth() + 1}/${ed.getDate()})`;

                        return `
                        <div class="absolute pointer-events-auto transition-all"
                             style="top: 8px; left: ${(d.startCol * 14.28)}%; width: ${(d.span * 14.28)}%; padding: 2px; z-index: 10;">
                            <div onclick="loadScheduleDetail(${d.event.id})" class="h-8 ${d.event.color || 'bg-indigo-500'} text-white rounded-full px-4 flex items-center font-black shadow-sm truncate border border-white/20 text-[10px] cursor-pointer hover:opacity-90 transition-opacity">
                               ${d.event.title} ${dateRange}
                            </div>
                        </div>`;
                    }).join('')}

                    ${totalMoreCount > 0 ? `
                        <div class="absolute pointer-events-auto"
                             style="top: 46px; left: ${(lastEndCol * 14.28)}%; width: 14.28%; display: flex; justify-content: center; z-index: 20;">
                            <div onclick="handleSideDayClick('${formatDate(startOfWeek)}', '${formatDate(endOfWeek)}', true)"
                                 class="px-2 py-0.5 bg-indigo-50 border border-indigo-100 rounded-full shadow-sm cursor-pointer hover:bg-indigo-100/80 hover:border-indigo-200 text-indigo-600 transition-all active:scale-95 select-none text-[9px] font-black text-center">
                                + ${totalMoreCount}개 더보기
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
                            <div onclick="loadScheduleDetail(${e.id})" class="p-2 bg-white border-2 border-rose-500 rounded-xl shadow-md cursor-pointer h-full flex flex-col justify-center overflow-hidden hover:shadow-lg transition-shadow">
                               <div class="flex items-center gap-1.5 w-full">
                                 <div class="w-2 h-2 bg-rose-500 rounded-full animate-pulse shrink-0"></div>
                                 <div class="min-w-0 flex-1">
                                   <p class="text-[11px] font-black text-gray-800 leading-tight truncate">${e.title}</p>
                                   <p class="text-[9px] font-bold text-rose-500 mt-0.5 uppercase truncate">~ ${formatTimeOnly(e.endDateTime)}</p>
                                 </div>
                               </div>
                            </div>
                        ` : `
                            <div onclick="loadScheduleDetail(${e.id})" class="p-3 ${e.color || 'bg-indigo-600'} text-white rounded-2xl shadow-lg transition-transform hover:scale-[1.02] cursor-pointer h-full overflow-hidden">
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

        if (response.status === 401 || response.redirected) {
            handleSessionTimeout();
            return;
        }

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

  document.querySelectorAll('#calendar-nav-bar button').forEach((btn, idx) => {
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
      if (!modal) return;

      document.getElementById('event-form').reset();

      document.getElementById('modal-mode-title').innerText = "새로운 일정 추가";
      document.getElementById('modal-mode-desc').innerText = "멋진 하루를 계획해 보세요!";
      document.getElementById('submit-btn-text').innerText = "일정 저장하기";

      document.getElementById('edit-mode-header').classList.add('hidden');
      document.getElementById('modal-status-checkbox-container').classList.add('hidden');

      document.getElementById('modal-target-type-container').classList.remove('hidden');
      document.getElementById('group-select-container').classList.add('hidden');

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

      const formMode = this.dataset.mode;
      const scheduleId = this.dataset.scheduleId;

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

      if (formMode === 'edit') {
          const completedCheckbox = document.getElementById('form-isCompleted');
          requestData.isCompleted = completedCheckbox ? completedCheckbox.checked : false;
      }

      if (!requestData.title.trim()) {
          showToast('error', '일정명을 입력해주세요.');
          return;
      }
      if (!requestData.endDateTime) {
          showToast('error', '종료 날짜를 선택해주세요.');
          return;
      }

      let apiUrl = '/api/schedules';
      let httpMethod = 'POST';

      if (formMode === 'edit' && scheduleId) {
          apiUrl = `/api/schedules/${scheduleId}`;
          httpMethod = 'PUT';
      }

      try {
          const response = await fetch(apiUrl, {
              method: httpMethod,
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

async function handleSideDayClick(dateStr, endDateStr = null, hasEvent = false) {
    const container = document.getElementById('side-schedules-container');
    const dateTitle = document.getElementById('side-selected-date');
    const listBody = document.getElementById('side-schedule-list');

    if (!container || !dateTitle || !listBody) return;

    if (endDateStr) {
        const startParts = dateStr.split('-');
        const endParts = endDateStr.split('-');
        dateTitle.innerText = `${parseInt(startParts[1])}월 ${parseInt(startParts[2])}일 ~ ${parseInt(endParts[1])}월 ${parseInt(endParts[2])}일`;
    } else {
        const dateParts = dateStr.split('-');
        dateTitle.innerText = `${dateParts[0]}년 ${dateParts[1]}월 ${dateParts[2]}일`;
    }

    const reqStartDate = dateStr;
    const reqEndDate = endDateStr ? endDateStr : dateStr;

    try {
        const response = await fetch(`/api/schedules?startDate=${reqStartDate}&endDate=${reqEndDate}`);

        if (response.status === 401 || response.redirected) {
            handleSessionTimeout();
            return;
        }

        const result = await response.json();

        if (response.ok && result.data && result.data.length > 0) {
            listBody.innerHTML = result.data.map(event => {

                const isGroup = event.groupId !== null && event.groupId !== undefined;
                const badgeText = isGroup ? '그룹' : '개인';

                const dbColor = event.color || 'bg-indigo-500';
                let bgClass = 'bg-indigo-50/40 border-indigo-100';
                let textClass = 'text-indigo-900';
                let badgeColor = 'text-indigo-600 border-indigo-100';
                let iconColor = 'text-indigo-500';

                if (dbColor.includes('bg-rose-500')) {
                    bgClass = 'bg-rose-50/40 border-rose-100';
                    textClass = 'text-rose-900';
                    badgeColor = 'text-rose-500 border-rose-100';
                    iconColor = 'text-rose-500';
                } else if (dbColor.includes('bg-emerald-500')) {
                    bgClass = 'bg-emerald-50/40 border-emerald-100';
                    textClass = 'text-emerald-900';
                    badgeColor = 'text-emerald-500 border-emerald-100';
                    iconColor = 'text-emerald-500';
                } else if (dbColor.includes('bg-gray-800')) {
                    bgClass = 'bg-slate-50 border-slate-200';
                    textClass = 'text-slate-900';
                    badgeColor = 'text-slate-600 border-slate-200';
                    iconColor = 'text-slate-500';
                }

                const timeLabel = formatSideTimeLabel(event.startDateTime, event.endDateTime);
                const serializedEvent = encodeURIComponent(JSON.stringify(event));

                return `
                    <div onclick="loadScheduleDetail(${event.id})"
                         class="p-4 ${bgClass} border rounded-2xl hover:shadow-md transition-all cursor-pointer group animate-in fade-in slide-in-from-bottom-2 duration-200">
                      <div class="flex items-center justify-between mb-1">
                        <span class="text-[10px] font-black uppercase tracking-tight ${textClass.replace('900', '600')}">${timeLabel}</span>
                        <span class="text-[9px] font-bold bg-white border ${badgeColor} px-1.5 py-0.5 rounded-md">${badgeText}</span>
                      </div>
                      <p class="text-xs font-bold ${textClass} truncate">${isGroup ? '👥 ' : '🔒 '}${event.title}</p>
                      ${event.location ? `
                        <p class="text-[10px] ${textClass.replace('900', '600')}/70 mt-1 font-medium flex items-center gap-1">
                          <i class="fa-solid fa-location-dot text-[9px] ${iconColor}"></i> ${event.location}
                        </p>
                      ` : ''}
                    </div>
                `;
            }).join('');
        }

        else {
            listBody.innerHTML = `
                <div class="py-12 px-4 text-center flex flex-col items-center justify-center animate-in fade-in zoom-in-95 duration-200 min-h-[280px] relative overflow-hidden">
                  <div class="w-14 h-14 bg-gray-50 rounded-full flex items-center justify-center text-gray-300 mb-4 border border-gray-100 shadow-inner">
                    <i class="fa-solid fa-calendar-xmark text-xl"></i>
                  </div>
                  <h5 class="text-xs font-bold text-gray-700">등록된 일정이 없습니다</h5>
                  <p class="text-[10px] text-gray-400 mt-1.5 font-medium tracking-tight">오늘 하루를 자유롭고 여유롭게 보내세요!</p>
                  <button onclick="openAddEventModal()" class="mt-6 px-4 py-2 bg-gray-50 border border-gray-200 hover:bg-indigo-50 hover:border-indigo-200 hover:text-indigo-600 text-gray-500 text-[10px] font-bold rounded-xl transition-all flex items-center gap-1.5 shadow-sm active:scale-95">
                    <i class="fa-solid fa-plus text-[8px]"></i> 이곳에 첫 일정 추가하기
                  </button>
                  <div class="absolute -bottom-6 -right-4 text-indigo-100/40 pointer-events-none select-none text-8xl -rotate-12">
                    <i class="fa-solid fa-mug-hot"></i>
                  </div>
                </div>
            `;
        }
    } catch (e) {
        console.error("사이드바 일정 로드 실패", e);
    }

    container.classList.remove('hidden');

    if (window.innerWidth < 1024) {
        container.scrollIntoView({ behavior: 'smooth', block: 'end' });
    }
}

function formatSideTimeLabel(startStr, endStr) {
    const e = new Date(endStr);
    if (!startStr) {
        return `~ ${formatTimeOnly(endStr)} 마감`;
    }

    const s = new Date(startStr);
    const sDateStr = `${s.getFullYear()}-${s.getMonth()}-${s.getDate()}`;
    const eDateStr = `${e.getFullYear()}-${e.getMonth()}-${e.getDate()}`;

    if (sDateStr === eDateStr) {
        const startM = s.getHours() < 12 ? '오전' : '오후';
        const startH = String(s.getHours() % 12 || 12).padStart(2, '0');
        const startMin = String(s.getMinutes()).padStart(2, '0');

        const endM = e.getHours() < 12 ? '오전' : '오후';
        const endH = String(e.getHours() % 12 || 12).padStart(2, '0');
        const endMin = String(e.getMinutes()).padStart(2, '0');

        if (startM === endM) {
            return `${startM} ${startH}:${startMin} - ${endH}:${endMin}`;
        }
        return `${startM} ${startH}:${startMin} - ${endM} ${endH}:${endMin}`;
    } else {
        const startMonth = String(s.getMonth() + 1).padStart(2, '0');
        const startDay = String(s.getDate()).padStart(2, '0');
        const endMonth = String(e.getMonth() + 1).padStart(2, '0');
        const endDay = String(e.getDate()).padStart(2, '0');
        return `${startMonth}/${startDay} ~ ${endMonth}/${endDay}`;
    }
}

function handleSessionTimeout() {

    showToast('error', '로그인 세션이 만료되었습니다. 다시 로그인해 주세요.');

    setTimeout(() => {
        window.location.href = '/members/login';
    }, 2000);
}

async function loadScheduleDetail(scheduleId) {
    try {
        const response = await fetch(`/api/schedules/${scheduleId}`);

        if (response.status === 401 || response.redirected) {
            handleSessionTimeout();
            return;
        }

        const result = await response.json();

        if (!response.ok) {
            const errorMessage = result && result.message ? result.message : "일정 상세 정보를 가져오는 데 실패했습니다.";
            showToast('error', errorMessage);
            return;
        }

        if (result.data) {
            openDetailEventModal(result.data);
        }
    } catch (e) {
        console.error("일정 상세 조회 중 에러 발생:", e);
        showToast('error', '서버와의 통신이 원활하지 않습니다.');
    }
}

function openDetailEventModal(event) {
    const modal = document.getElementById('detail-event-modal');
    if (!modal) return;

    const statusUncheckIcon = document.getElementById('detail-status-uncheck');
    const statusCheckedIcon = document.getElementById('detail-status-checked');
    const statusTextSpan = document.getElementById('detail-status-text');

    if (statusUncheckIcon && statusCheckedIcon && statusTextSpan) {
        if (event.isCompleted === true) {
            statusUncheckIcon.classList.add('hidden');
            statusCheckedIcon.classList.remove('hidden');
            statusTextSpan.innerText = "완료";
            statusTextSpan.className = "text-xs font-bold text-indigo-600 tracking-tight";
        } else {
            statusCheckedIcon.classList.add('hidden');
            statusUncheckIcon.classList.remove('hidden');
            statusTextSpan.innerText = "진행 중";
            statusTextSpan.className = "text-xs font-medium text-gray-950 tracking-tight";
        }
    }

    const titleEl = modal.querySelector('#detail-title');
    if (titleEl) titleEl.innerText = event.title;

    const timeEl = modal.querySelector('#detail-datetime');
    if (timeEl) {
        timeEl.innerText = formatDetailTimeLabel(event.startDateTime, event.endDateTime);
    }

    const targetTypeBadge = modal.querySelector('#detail-targetType');
    const isGroup = event.groupId !== null && event.groupId !== undefined;
    if (targetTypeBadge) {
        if (isGroup) {
            targetTypeBadge.innerText = "👥 그룹 일정";
            targetTypeBadge.className = "inline-flex items-center gap-1 text-[10px] font-black text-indigo-600 bg-indigo-50 px-3 py-1 rounded-full uppercase tracking-tighter";
        } else {
            targetTypeBadge.innerText = "🔒 개인 일정";
            targetTypeBadge.className = "inline-flex items-center gap-1 text-[10px] font-black text-gray-600 bg-gray-100 px-3 py-1 rounded-full uppercase tracking-tighter";
        }
    }

    const locationEl = modal.querySelector('#detail-location');
    if (locationEl) {
        const locationRow = locationEl.closest('.flex.items-baseline');
        if (locationRow && locationRow.parentElement) {
            const outmostLocationDiv = locationRow.parentElement;
            if (event.location && event.location.trim() !== '') {
                locationEl.innerText = event.location;
                outmostLocationDiv.classList.remove('hidden');
            } else {
                outmostLocationDiv.classList.add('hidden');
            }
        }
    }

    const contentEl = modal.querySelector('#detail-content');
    if (contentEl) {
        const contentRow = contentEl.closest('.flex.items-baseline');
        if (contentRow && contentRow.parentElement) {
            const outmostContentDiv = contentRow.parentElement;
            if (event.content && event.content.trim() !== '') {
                contentEl.innerText = event.content;
                outmostContentDiv.classList.remove('hidden');
            } else {
                outmostContentDiv.classList.add('hidden');
            }
        }
    }

    const groupItemRow = document.getElementById('detail-group-item');
    const creatorNameSpan = document.getElementById('detail-creator-name');
    const commentSection = document.getElementById('detail-comment-section');

    const creatorRow = creatorNameSpan ? creatorNameSpan.closest('.flex.items-center.gap-3') : null;
    const outmostCreatorDiv = creatorRow ? creatorRow.parentElement : null;
    const timeRow = timeEl ? timeEl.closest('.flex.items-baseline').parentElement : null;

    if (isGroup) {
        if (timeRow) timeRow.classList.remove('!mt-0');
        if (groupItemRow) groupItemRow.classList.remove('hidden');
        if (outmostCreatorDiv) outmostCreatorDiv.classList.remove('hidden');
        if (commentSection) commentSection.classList.remove('hidden');

        const groupNameSpan = document.getElementById('detail-group-name');
        if (groupNameSpan) groupNameSpan.innerText = event.groupName || "소속 그룹 없음";

        if (creatorNameSpan) creatorNameSpan.innerText = event.profileName || "정보 없음";

        const groupImage = document.getElementById('detail-group-image');
        const groupIcon = document.getElementById('detail-group-icon');
        if (groupImage && groupIcon) {
            if (event.groupImage && event.groupImage.trim() !== "") {
                groupImage.src = '/group/' + event.groupImage;
                groupImage.classList.remove('hidden');
                groupIcon.classList.add('hidden');
            } else {
                groupIcon.classList.remove('hidden');
                groupImage.classList.add('hidden');
            }
        }

        let profileImgEl = modal.querySelector('#detail-profile-image');
        let defaultUserIcon = creatorRow ? creatorRow.querySelector('.fa-user') : null;

        if (event.profileImage && event.profileImage.trim() !== "") {
            if (!profileImgEl && defaultUserIcon) {
                profileImgEl = document.createElement('img');
                profileImgEl.id = 'detail-profile-image';
                profileImgEl.className = 'w-5 h-5 rounded-full object-cover border border-gray-100 shadow-sm shrink-0 mr-1.5';
                defaultUserIcon.parentElement.insertBefore(profileImgEl, creatorNameSpan);
            }
            if (profileImgEl) {
                profileImgEl.src = '/member/' + event.profileImage;
                profileImgEl.classList.remove('hidden');
            }
            if (defaultUserIcon) defaultUserIcon.classList.add('hidden');
        } else {
            if (profileImgEl) profileImgEl.classList.add('hidden');
            if (defaultUserIcon) defaultUserIcon.classList.remove('hidden');
        }

        const commentCountSpan = document.getElementById('detail-comment-count');
        const commentListDiv = document.getElementById('detail-comment-list');
        const commentPaginationDiv = document.getElementById('comment-pagination');

        if (event.comments && event.comments.length > 0) {
            if (commentCountSpan) {
                commentCountSpan.innerText = event.totalCommentCount || 0;
            }

            if (commentPaginationDiv) {
                if ((event.totalCommentCount || 0) <= 5) {
                    commentPaginationDiv.classList.add('hidden');
                } else {
                    commentPaginationDiv.classList.remove('hidden');
                    renderCommentPagination(0, event.totalCommentCount);
                }
            }

            if (commentListDiv) {
                commentListDiv.innerHTML = event.comments.map(comment => {
                    const profileSrc = comment.profileImage && comment.profileImage.trim() !== ""
                        ? '/member/' + comment.profileImage
                        : '/images/default-profile.png';

                    let dateStr = comment.createdDate;
                    if (dateStr && typeof dateStr === 'string') {
                        if (dateStr.includes('.')) {
                            dateStr = dateStr.split('.')[0];
                        }
                        dateStr = dateStr.replace('T', ' ').replace(/-/g, '/');
                    }

                    const rawDate = new Date(dateStr);
                    const nowDate = new Date();

                    let timeLabel = '';

                    if (!isNaN(rawDate.getTime())) {
                        const isToday = rawDate.getFullYear() === nowDate.getFullYear() &&
                                        rawDate.getMonth() === nowDate.getMonth() &&
                                        rawDate.getDate() === nowDate.getDate();

                        if (isToday) {
                            const hours = rawDate.getHours();
                            const minutes = String(rawDate.getMinutes()).padStart(2, '0');
                            const ampm = hours < 12 ? '오전' : '오후';
                            const displayHour = String(hours % 12 || 12).padStart(2, '0');
                            timeLabel = `${ampm} ${displayHour}:${minutes}`;
                        } else {
                            const year = rawDate.getFullYear();
                            const month = String(rawDate.getMonth() + 1).padStart(2, '0');
                            const day = String(rawDate.getDate()).padStart(2, '0');
                            timeLabel = `${year}-${month}-${day}`;
                        }
                    } else {
                        timeLabel = comment.createdAt ? comment.createdAt.substring(0, 10) : '';
                    }

                    return `
                        <div id="comment-item-${comment.id}" class="group p-5 bg-gray-50 border border-gray-100 rounded-[1.5rem] flex items-start gap-4 transition-all hover:bg-white hover:shadow-md">
                            <img src="${profileSrc}" class="w-9 h-9 rounded-full object-cover shrink-0 shadow-sm border border-gray-100">
                            <div class="flex-1 min-w-0 space-y-1.5">
                                <div class="flex items-center justify-between">
                                    <div class="flex items-center gap-2">
                                        <span class="text-[12px] font-bold text-gray-900">${comment.profileName}</span>
                                        <span class="text-[9px] font-medium text-gray-400 uppercase tracking-tight">${timeLabel}</span>
                                    </div>
                                    <div class="opacity-0 group-hover:opacity-100 flex items-center gap-2 transition-all">
                                        ${comment.isOwner ? `
                                            <button type="button" onclick="toggleEditCommentForm(${comment.id})" title="댓글 수정" class="text-gray-400 hover:text-indigo-500 transition-colors"><i class="fa-solid fa-pen text-[10px]"></i></button>
                                            <button type="button" onclick="openDeleteCommentModal(${comment.id})" title="댓글 삭제" class="text-gray-400 hover:text-rose-500 transition-colors"><i class="fa-solid fa-trash-can text-[10px]"></i></button>
                                        ` : ''}
                                    </div>
                                </div>
                                <p id="comment-text-${comment.id}" class="text-[12px] text-gray-600 font-medium leading-relaxed block">${comment.content}</p>
                                <div id="comment-edit-box-${comment.id}" class="hidden mt-2 space-y-2">
                                    <textarea id="comment-edit-input-${comment.id}" rows="2" class="w-full p-3 bg-white border border-gray-200 rounded-xl text-[12px] font-medium leading-relaxed focus:ring-1 focus:ring-black focus:outline-none transition-all resize-none">${comment.content}</textarea>
                                    <div class="flex justify-end gap-1.5">
                                        <button type="button" onclick="toggleEditCommentForm(${comment.id})" class="px-3 py-1.5 bg-gray-200 text-gray-600 text-[10px] font-bold rounded-lg hover:bg-gray-300 transition-colors">취소</button>
                                        <button type="button" onclick="saveCommentEdit(${comment.id})" class="px-3 py-1.5 bg-gray-900 text-white text-[10px] font-bold rounded-lg hover:bg-black transition-colors">저장</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                }).join('');
            }
        } else {
            if (commentCountSpan) commentCountSpan.innerText = "0";
            if (commentPaginationDiv) {
                if ((event.totalCommentCount || 0) <= 5) {
                    commentPaginationDiv.classList.add('hidden');
                } else {
                    commentPaginationDiv.classList.remove('hidden');
                    renderCommentPagination(event.currentPage, event.totalCommentCount);
                }
            }
            if (commentListDiv) {
                commentListDiv.innerHTML = `
                    <div id="comment-empty-state" class="py-8 px-4 text-center flex flex-col items-center justify-center animate-in fade-in zoom-in-95 duration-200 border border-dashed border-gray-200 rounded-[1.5rem] relative overflow-hidden bg-gray-50/30">
                        <div class="w-10 h-10 bg-white rounded-full flex items-center justify-center text-gray-300 mb-2 border border-gray-100 shadow-inner">
                            <i class="fa-regular fa-comments text-base"></i>
                        </div>
                        <h5 class="text-[11px] font-bold text-gray-400">아직 등록된 댓글이 없습니다</h5>
                        <p class="text-[10px] text-gray-400/70 mt-0.5 font-medium tracking-tight">이 일정에 대한 첫 번째 의견을 남겨보세요!</p>
                        <div class="absolute -bottom-4 -right-3 text-indigo-100/30 pointer-events-none select-none text-6xl -rotate-12">
                            <i class="fa-solid fa-comment-dots"></i>
                        </div>
                    </div>
                `;
            }
        }
    } else {
        if (timeRow) timeRow.classList.add('!mt-0');
        if (groupItemRow) groupItemRow.classList.add('hidden');
        if (outmostCreatorDiv) outmostCreatorDiv.classList.add('hidden');
        if (commentSection) commentSection.classList.add('hidden');
    }

    const actionButtonContainer = modal.querySelector('.p-8.bg-white.border-t.border-gray-100.shrink-0.flex');
    if (actionButtonContainer) {
        if (event.isOwner === false) {
            actionButtonContainer.innerHTML = `
                <button type="button" onclick="closeDetailEventModal()" class="w-full py-3.5 bg-gray-900 text-white font-bold rounded-xl hover:bg-black shadow-lg shadow-gray-200 transition-all text-sm tracking-tight">
                    닫기
                </button>
            `;
        } else {
            actionButtonContainer.innerHTML = `
                <button type="button" onclick="openDeleteScheduleModal()" class="flex-1 py-3.5 border border-gray-200 text-rose-500 hover:bg-rose-50 font-bold rounded-xl transition-all text-sm tracking-tight bg-white">
                    삭제
                </button>
                <button type="button" onclick='handleEditScheduleTrigger(${JSON.stringify(event)})' class="flex-[2] py-3.5 bg-gray-900 text-white font-bold rounded-xl hover:bg-black shadow-lg shadow-gray-200 transition-all text-sm tracking-tight">
                    수정하기
                </button>
            `;
        }
    }

    modal.dataset.currentScheduleId = event.id;

    const commentWriteContainer = document.getElementById('comment-write-container');
    if (commentWriteContainer) {
        if (isGroup) {
            commentWriteContainer.classList.remove('hidden');
        } else {
            commentWriteContainer.classList.add('hidden');
        }
    }

    modal.classList.remove('hidden');
    modal.classList.add('flex');
    document.body.style.overflow = 'hidden';
}

function formatDetailTimeLabel(startStr, endStr) {
    const e = new Date(endStr);

    if (!startStr) {
        const m = String(e.getMonth() + 1).padStart(2, '0');
        const d = String(e.getDate()).padStart(2, '0');
        const ampm = e.getHours() < 12 ? '오전' : '오후';
        const h = String(e.getHours() % 12 || 12).padStart(2, '0');
        const min = String(e.getMinutes()).padStart(2, '0');
        return `${m}월 ${d}일 ${ampm} ${h}:${min} 까지`;
    }

    const s = new Date(startStr);
    const sDateStr = `${s.getFullYear()}-${s.getMonth()}-${s.getDate()}`;
    const eDateStr = `${e.getFullYear()}-${e.getMonth()}-${e.getDate()}`;

    if (sDateStr === eDateStr) {
        const sM = s.getHours() < 12 ? '오전' : '오후';
        const sH = String(s.getHours() % 12 || 12).padStart(2, '0');
        const sMin = String(s.getMinutes()).padStart(2, '0');

        const eM = e.getHours() < 12 ? '오전' : '오후';
        const eH = String(e.getHours() % 12 || 12).padStart(2, '0');
        const eMin = String(e.getMinutes()).padStart(2, '0');

        return `${s.getMonth()+1}월 ${s.getDate()}일 ${sM} ${sH}:${sMin} ~ ${eM} ${eH}:${eMin}`;
    }
    else {
        const sM = String(s.getMonth() + 1).padStart(2, '0');
        const sD = String(s.getDate()).padStart(2, '0');
        const eM = String(e.getMonth() + 1).padStart(2, '0');
        const eD = String(e.getDate()).padStart(2, '0');
        return `${sM}/${sD} ~ ${eM}/${eD}`;
    }
}

function closeDetailEventModal() {
    const modal = document.getElementById('detail-event-modal');
    if (!modal) return;

    modal.classList.add('hidden');
    document.body.style.overflow = 'auto';
}

function toggleEditCommentForm(commentId) {
    const textParagraph = document.getElementById(`comment-text-${commentId}`);
    const editBox = document.getElementById(`comment-edit-box-${commentId}`);

    if (!textParagraph || !editBox) return;

    if (editBox.classList.contains('hidden')) {
        editBox.classList.remove('hidden');
        textParagraph.classList.add('hidden');
    } else {
        editBox.classList.add('hidden');
        textParagraph.classList.remove('hidden');
    }
}

async function saveCommentEdit(commentId) {
    const modal = document.getElementById('detail-event-modal');
    const scheduleId = modal.dataset.currentScheduleId;
    const editInput = document.getElementById(`comment-edit-input-${commentId}`);

    if (!scheduleId || !editInput) return;

    const updatedContent = editInput.value.trim();

    if (!updatedContent) {
        showToast('error', '댓글 내용을 입력해 주세요.');
        return;
    }

    try {
        const response = await fetch(`/api/schedules/${scheduleId}/comments/${commentId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ content: updatedContent })
        });

        const result = await response.json();

        if (response.ok) {
            showToast('success', result.message);

            const pageNumbersDiv = document.getElementById('comment-page-numbers');
            let currentPage = 0;

            if (pageNumbersDiv) {
                const activeBtn = pageNumbersDiv.querySelector('.bg-gray-900');
                if (activeBtn) {
                    currentPage = parseInt(activeBtn.innerText) - 1;
                }
            }

            setTimeout(async () => {
                await changeCommentPage(currentPage);
            }, 1500);
        } else {
            showToast('error', result.message);
        }
    } catch (error) {
        console.error('Comment Edit Error:', error);
        showToast('error', '서버와의 통신에 실패했습니다.');
    }
}

function openDeleteCommentModal(commentId) {
    currentDeleteCommentId = commentId;

    const modal = document.getElementById('deleteCommentConfirmModal');
    if (modal) {
        modal.classList.remove('hidden');
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }
}

function closeDeleteCommentModal(e) {
    if (e) {
        if (typeof e.preventDefault === 'function') e.preventDefault();
        if (typeof e.stopPropagation === 'function') e.stopPropagation();
    }

    const modal = document.getElementById('deleteCommentConfirmModal');
    if (modal) {
        modal.classList.add('hidden');
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

function openDeleteScheduleModal() {
    const modal = document.getElementById('deleteScheduleConfirmModal');
    if (modal) {
        modal.classList.remove('hidden');
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }
}

function closeDeleteScheduleModal() {
    const modal = document.getElementById('deleteScheduleConfirmModal');
    if (modal) {
        modal.classList.add('hidden');
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

function handleEditScheduleTrigger(event) {
    closeDetailEventModal();
    document.body.style.overflow = 'hidden';

    const eventModal = document.getElementById('event-modal');
    if (!eventModal) return;

    document.getElementById('modal-mode-title').innerText = "일정 정보 수정";
    document.getElementById('modal-mode-desc').innerText = "기존 일정의 세부 내용을 변경합니다.";
    document.getElementById('submit-btn-text').innerText = "수정 완료하기";

    document.getElementById('modal-target-type-container').classList.add('hidden');
    document.getElementById('group-select-container').classList.add('hidden');

    document.getElementById('form-title').value = event.title || '';
    document.getElementById('form-content').value = event.content || '';
    document.getElementById('form-location').value = event.location || '';

    if (event.startDateTime) {
        const startParts = event.startDateTime.split('T');
        document.getElementById('startDate').value = startParts[0];
        if (startParts[1]) {
            document.getElementById('startTime').value = startParts[1].substring(0, 5);
        }
    } else {
        document.getElementById('startDate').value = '';
        document.getElementById('startTime').value = '';
    }

    if (event.endDateTime) {
        const endParts = event.endDateTime.split('T');
        document.getElementById('endDate').value = endParts[0];
        if (endParts[1]) {
            document.getElementById('endTime').value = endParts[1].substring(0, 5);
        }
    }

    const colorRadio = document.querySelector(`input[name="color"][value="${event.color}"]`);
    if (colorRadio) {
        colorRadio.checked = true;
    }

    const completedCheckbox = document.getElementById('form-isCompleted');
    if (completedCheckbox) {
        completedCheckbox.checked = event.isCompleted === true;
    }

    const editHeader = document.getElementById('edit-mode-header');
    const editIcon = document.getElementById('edit-mode-icon');
    const editLabel = document.getElementById('edit-mode-label');
    const editValue = document.getElementById('edit-mode-value');
    const isGroupEvent = event.groupId !== null && event.groupId !== undefined;

    if (editHeader) {
        editHeader.classList.remove('hidden');
        if (isGroupEvent) {
            editHeader.classList.remove('bg-gray-50', 'border-gray-100');
            editHeader.classList.add('bg-indigo-50/50', 'border-indigo-100');
            editIcon.className = "fa-solid fa-users text-xs text-indigo-500";
            editLabel.className = "text-xs font-bold text-gray-600 shrink-0";
            editLabel.innerText = "소속 그룹 :";
            editValue.className = "text-xs font-black text-indigo-700";
            editValue.innerText = event.groupName || "소속 그룹 없음";
        } else {
            editHeader.classList.remove('bg-indigo-50/50', 'border-indigo-100');
            editHeader.classList.add('bg-gray-50', 'border-gray-100');
            editIcon.className = "fa-solid fa-lock text-xs text-gray-400";
            editLabel.className = "text-xs font-bold text-gray-500 shrink-0";
            editLabel.innerText = "일정 종류 :";
            editValue.className = "text-xs font-bold text-gray-800";
            editValue.innerText = "🔒 개인 일정";
        }
    }

    document.getElementById('modal-status-checkbox-container').classList.remove('hidden');

    const form = document.getElementById('event-form');
    form.dataset.mode = 'edit';
    form.dataset.scheduleId = event.id;

    eventModal.classList.remove('hidden');
    eventModal.classList.add('flex');
}

function closeAddEventModal() {
    const modal = document.getElementById('event-modal');
    const groupContainer = document.getElementById('group-select-container');

    if (modal) {
        modal.classList.replace('flex', 'hidden');
        document.body.style.overflow = 'auto';

        document.getElementById('event-form').reset();

        document.getElementById('modal-mode-title').innerText = "새로운 일정 추가";
        document.getElementById('modal-mode-desc').innerText = "멋진 하루를 계획해 보세요!";
        document.getElementById('submit-btn-text').innerText = "일정 저장하기";

        document.getElementById('modal-target-type-container').classList.remove('hidden');
        document.getElementById('edit-mode-group-header').classList.add('hidden');
        document.getElementById('modal-status-checkbox-container').classList.add('hidden');

        if (groupContainer) {
            groupContainer.classList.add('hidden');
        }
    }
}

document.getElementById('btn-comment-submit').onclick = async function() {
    const modal = document.getElementById('detail-event-modal');
    const scheduleId = modal.dataset.currentScheduleId;
    const commentInput = document.getElementById('comment-input');

    if (!commentInput || !scheduleId) return;

    const contentValue = commentInput.value.trim();

    if (!contentValue) {
        showToast('error', '댓글 내용을 입력해 주세요.');
        return;
    }

    try {
        const response = await fetch(`/api/schedules/${scheduleId}/comments`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ content: contentValue })
        });

        const result = await response.json();

        if (response.ok) {
            showToast('success', result.message);
            commentInput.value = '';

            setTimeout(async () => {
                const refreshResponse = await fetch(`/api/schedules/${scheduleId}`);
                if (refreshResponse.ok) {
                    const refreshResult = await refreshResponse.json();
                    openDetailEventModal(refreshResult.data);
                }
            }, 1500);
        } else {
            showToast('error', result.message);
        }
    } catch (error) {
        console.error('Comment Register Error:', error);
        showToast('error', '서버와의 통신에 실패했습니다.');
    }
};

document.getElementById('comment-input').onkeydown = function(e) {
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        document.getElementById('btn-comment-submit').click();
    }
};

function renderCommentPagination(currentPage, totalCommentCount) {
    const pageNumbersDiv = document.getElementById('comment-page-numbers');
    const btnPrev = document.getElementById('btn-comment-prev');
    const btnNext = document.getElementById('btn-comment-next');

    if (!pageNumbersDiv) return;

    const size = 5;
    const totalPages = Math.ceil(totalCommentCount / size);

    if (btnPrev) {
        if (currentPage === 0) {
            btnPrev.classList.add('opacity-30', 'pointer-events-none');
            btnPrev.onclick = null;
        } else {
            btnPrev.classList.remove('opacity-30', 'pointer-events-none');
            btnPrev.onclick = () => changeCommentPage(currentPage - 1);
        }
    }

    if (btnNext) {
        if (currentPage + 1 === totalPages || totalPages === 0) {
            btnNext.classList.add('opacity-30', 'pointer-events-none');
            btnNext.onclick = null;
        } else {
            btnNext.classList.remove('opacity-30', 'pointer-events-none');
            btnNext.onclick = () => changeCommentPage(currentPage + 1);
        }
    }

    let html = '';
    for (let i = 1; i <= totalPages; i++) {
        if ((i - 1) === currentPage) {
            html += `<button type="button" class="w-8 h-8 flex items-center justify-center rounded-xl text-[11px] font-extrabold bg-gray-900 text-white shadow-md shadow-gray-200 translate-y-[-1px]">${i}</button>`;
        } else {
            html += `<button type="button" onclick="changeCommentPage(${i - 1})" class="w-8 h-8 flex items-center justify-center rounded-xl text-[11px] font-medium text-gray-400 hover:bg-gray-100 hover:text-gray-900 transition-all">${i}</button>`;
        }
    }

    pageNumbersDiv.innerHTML = html;
}

async function changeCommentPage(targetPage) {
    const modal = document.getElementById('detail-event-modal');
    const scheduleId = modal.dataset.currentScheduleId;

    if (!scheduleId) return;

    try {
        const response = await fetch(`/api/schedules/${scheduleId}/comments?page=${targetPage}&size=5`);

        if (response.status === 401 || response.redirected) {
            handleSessionTimeout();
            return;
        }

        if (response.ok) {
            const result = await response.json();

            const commentCountSpan = document.getElementById('detail-comment-count');
            if (commentCountSpan) {
                commentCountSpan.innerText = result.data.totalCommentCount || 0;
            }

            const commentPaginationDiv = document.getElementById('comment-pagination');
            if (commentPaginationDiv) {
                if ((result.data.totalCommentCount || 0) <= 5) {
                    commentPaginationDiv.classList.add('hidden');
                } else {
                    commentPaginationDiv.classList.remove('hidden');
                    renderCommentPagination(result.data.currentPage, result.data.totalCommentCount);
                }
            }

            const commentListDiv = document.getElementById('detail-comment-list');
            const pageNumbersDiv = document.getElementById('comment-page-numbers');

            if (commentListDiv) {
                if (!result.data.comments || result.data.comments.length === 0) {
                    commentListDiv.innerHTML = `
                        <div id="comment-empty-state" class="py-8 px-4 text-center flex flex-col items-center justify-center animate-in fade-in zoom-in-95 duration-200 border border-dashed border-gray-200 rounded-[1.5rem] relative overflow-hidden bg-gray-50/30">
                            <div class="w-10 h-10 bg-white rounded-full flex items-center justify-center text-gray-300 mb-2 border border-gray-100 shadow-inner">
                                <i class="fa-regular fa-comments text-base"></i>
                            </div>
                            <h5 class="text-[11px] font-bold text-gray-400">아직 등록된 댓글이 없습니다</h5>
                            <p class="text-[10px] text-gray-400/70 mt-0.5 font-medium tracking-tight">이 일정에 대한 첫 번째 의견을 남겨보세요!</p>
                            <div class="absolute -bottom-4 -right-3 text-indigo-100/30 pointer-events-none select-none text-6xl -rotate-12">
                                <i class="fa-solid fa-comment-dots"></i>
                            </div>
                        </div>
                    `;
                    if (pageNumbersDiv) pageNumbersDiv.innerHTML = '';
                    return;
                }

                commentListDiv.innerHTML = result.data.comments.map(comment => {
                    const profileSrc = comment.profileImage && comment.profileImage.trim() !== ""
                        ? '/member/' + comment.profileImage
                        : '/images/default-profile.png';

                    let dateStr = comment.createdDate;
                    if (dateStr && typeof dateStr === 'string') {
                        if (dateStr.includes('.')) {
                            dateStr = dateStr.split('.')[0];
                        }
                        dateStr = dateStr.replace('T', ' ').replace(/-/g, '/');
                    }

                    const rawDate = new Date(dateStr);
                    const nowDate = new Date();
                    let timeLabel = '';

                    if (!isNaN(rawDate.getTime())) {
                        const isToday = rawDate.getFullYear() === nowDate.getFullYear() &&
                                        rawDate.getMonth() === nowDate.getMonth() &&
                                        rawDate.getDate() === nowDate.getDate();

                        if (isToday) {
                            const hours = rawDate.getHours();
                            const minutes = String(rawDate.getMinutes()).padStart(2, '0');
                            const ampm = hours < 12 ? '오전' : '오후';
                            const displayHour = String(hours % 12 || 12).padStart(2, '0');
                            timeLabel = `${ampm} ${displayHour}:${minutes}`;
                        } else {
                            const year = rawDate.getFullYear();
                            const month = String(rawDate.getMonth() + 1).padStart(2, '0');
                            const day = String(rawDate.getDate()).padStart(2, '0');
                            timeLabel = `${year}-${month}-${day}`;
                        }
                    } else {
                        timeLabel = comment.createdDate ? comment.createdDate.substring(0, 10) : '';
                    }

                    return `
                        <div id="comment-item-${comment.id}" class="group p-5 bg-gray-50 border border-gray-100 rounded-[1.5rem] flex items-start gap-4 transition-all hover:bg-white hover:shadow-md">
                            <img src="${profileSrc}" class="w-9 h-9 rounded-full object-cover shrink-0 shadow-sm border border-gray-100">
                            <div class="flex-1 min-w-0 space-y-1.5">
                                <div class="flex items-center justify-between">
                                    <div class="flex items-center gap-2">
                                        <span class="text-[12px] font-bold text-gray-900">${comment.profileName}</span>
                                        <span class="text-[9px] font-medium text-gray-400 uppercase tracking-tight">${timeLabel}</span>
                                    </div>
                                    <div class="opacity-0 group-hover:opacity-100 flex items-center gap-2 transition-all">
                                        ${comment.isOwner ? `
                                            <button type="button" onclick="toggleEditCommentForm(${comment.id})" title="댓글 수정" class="text-gray-400 hover:text-indigo-500 transition-colors"><i class="fa-solid fa-pen text-[10px]"></i></button>
                                            <button type="button" onclick="openDeleteCommentModal(${comment.id})" title="댓글 삭제" class="text-gray-400 hover:text-rose-500 transition-colors"><i class="fa-solid fa-trash-can text-[10px]"></i></button>
                                        ` : ''}
                                    </div>
                                </div>
                                <p id="comment-text-${comment.id}" class="text-[12px] text-gray-600 font-medium leading-relaxed block">${comment.content}</p>
                                <div id="comment-edit-box-${comment.id}" class="hidden mt-2 space-y-2">
                                    <textarea id="comment-edit-input-${comment.id}" rows="2" class="w-full p-3 bg-white border border-gray-200 rounded-xl text-[12px] font-medium leading-relaxed focus:ring-1 focus:ring-black focus:outline-none transition-all resize-none">${comment.content}</textarea>
                                    <div class="flex justify-end gap-1.5">
                                        <button type="button" onclick="toggleEditCommentForm(${comment.id})" class="px-3 py-1.5 bg-gray-200 text-gray-600 text-[10px] font-bold rounded-lg hover:bg-gray-300 transition-colors">취소</button>
                                        <button type="button" onclick="saveCommentEdit(${comment.id})" class="px-3 py-1.5 bg-gray-900 text-white text-[10px] font-bold rounded-lg hover:bg-black transition-colors">저장</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                }).join('');
            }
        }
    } catch (error) {
        console.error('Comment Page Change Error:', error);
    }
}

async function executeCommentDelete() {
    if (!currentDeleteCommentId) return;

    const modal = document.getElementById('detail-event-modal');
    const scheduleId = modal.dataset.currentScheduleId;

    if (!scheduleId) return;

    try {
        const response = await fetch(`/api/schedules/${scheduleId}/comments/${currentDeleteCommentId}`, {
            method: 'DELETE'
        });

        const result = await response.json();

        if (response.ok) {
            showToast('success', result.message);

            closeDeleteCommentModal();

            const pageNumbersDiv = document.getElementById('comment-page-numbers');
            let currentPage = 0;

            if (pageNumbersDiv) {
                const activeBtn = pageNumbersDiv.querySelector('.bg-gray-900');
                if (activeBtn) {
                    currentPage = parseInt(activeBtn.innerText) - 1;
                }
            }

            setTimeout(async () => {
                await changeCommentPage(currentPage);
            }, 1500);

        } else {
            showToast('error', result.message);
            closeDeleteCommentModal();
        }
    } catch (error) {
        console.error('Comment Delete Error:', error);
        showToast('error', '서버와의 통신에 실패했습니다.');
        closeDeleteCommentModal();
    } finally {
        currentDeleteCommentId = null;
    }
}