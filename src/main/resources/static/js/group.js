let groupCalendarYear = new Date().getFullYear();
let groupCalendarMonth = new Date().getMonth() + 1;
let groupAllSchedulesCached = []; // 달력 전역 트래픽 캐싱용 버퍼

document.addEventListener('DOMContentLoaded', function() {
    const editForm = document.getElementById('editGroupForm');
    if (editForm) {
        editForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            if (!this.name.value.trim()) {
                showToast('error', '이름을 입력하세요.');
                return;
            }

            const groupId = this.dataset.groupId;
            const formData = new FormData();

            const groupUpdateData = {
                name: this.name.value,
                description: this.description.value
            };

            formData.append('data', new Blob([JSON.stringify(groupUpdateData)], {
                type: 'application/json'
            }));

            const imageInput = document.getElementById('editImageInput');
            if (imageInput.files[0]) {
                formData.append('image', imageInput.files[0]);
            }

            try {
                const response = await fetch(`/api/groups/${groupId}`, {
                    method: 'PATCH',
                    body: formData,
                });

                const result = await response.json();

                if (response.ok) {
                    showToast('success', result.message);
                    setTimeout(() => { location.reload(); }, 2000);
                } else {
                    showToast('error', result.message);
                }
            } catch (error) {
                console.error("Error updating group:", error);
                alert("서버 통신 중 오류가 발생했습니다.");
            }
        });
    }

    const createLinkForm = document.getElementById('createLinkForm');
    if (createLinkForm) {
        createLinkForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const pathSegments = window.location.pathname.split('/');
            const groupId = pathSegments[pathSegments.indexOf('groups') + 1];

            const expiredAt = this.duration.value;

            try {
                const response = await fetch(`/api/groups/${groupId}/invitation-link?expiredAt=${expiredAt}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });

                const result = await response.json();

                if (response.ok) {
                    showToast('success', result.message);
                    setTimeout(() => { location.reload(); }, 2000);
                } else {
                    showToast('error', result.message);
                    setTimeout(() => { location.reload(); }, 2000);
                }
            } catch (error) {
                console.error("Error creating invitation link:", error);
                alert("서버 통신 중 오류가 발생했습니다.");
            }
        });
    }

    initGroupCalendarConfig();
});

function openEditModal() {
    document.getElementById('editGroupModal').classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function closeEditModal() {
    document.getElementById('editGroupModal').classList.add('hidden');
    document.body.style.overflow = 'auto';
}

function previewEditImage(input) {
    const container = document.getElementById('editPreviewContainer');
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            container.innerHTML = `
                <img src="${e.target.result}" class="w-full h-full object-cover shadow-sm" alt="미리보기">
                <div class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center text-white text-[10px] font-bold pointer-events-none uppercase tracking-widest">
                    변경하기
                </div>
            `;
        }
        reader.readAsDataURL(input.files[0]);
    }
}

function resetToDefault() {
    const container = document.getElementById('editPreviewContainer');
    const input = document.getElementById('editImageInput');
    container.innerHTML = `
        <i class="fa-solid fa-users text-4xl text-gray-300"></i>
        <div class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center text-white text-[10px] font-bold pointer-events-none uppercase tracking-widest">
            변경하기
        </div>
    `;
    input.value = "";
}

function changeSort(sortValue) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set('sort', sortValue);
    urlParams.set('page', '0');
    window.location.href = window.location.pathname + '?' + urlParams.toString();
}

function openCreateLinkModal() {
    document.getElementById('createLinkModal').classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function closeCreateLinkModal() {
    document.getElementById('createLinkModal').classList.add('hidden');
    document.body.style.overflow = 'auto';
}

function copyLink(element) {
    const code = element.getAttribute('data-code');
    const fullUrl = window.location.origin + '/groups/join?code=' + code;
    navigator.clipboard.writeText(fullUrl).then(() => {
        if (typeof showToast === 'function') showToast('success', '초대 링크가 클립보드에 복사되었습니다.');
    });
}

let linkToDelete = { groupId: null, linkId: null };

function openDeleteLinkModal(element) {
    linkToDelete.linkId = element.getAttribute('data-link-id');
    linkToDelete.groupId = element.getAttribute('data-group-id');

    document.getElementById('deleteLinkConfirmModal').classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

document.getElementById('confirmDeleteLinkBtn')?.addEventListener('click', async function() {
    const { groupId, linkId } = linkToDelete;

    try {
        const response = await fetch(`/api/groups/${groupId}/invitation-link/${linkId}`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' }
        });

        const result = await response.json();

        if (response.ok) {
            showToast('success', result.message);
            setTimeout(() => { location.reload(); }, 2000);
        } else {
            showToast('error', result.message);
        }
    } catch (error) {
        console.error("Error:", error);
        showToast('error', '서버 통신 오류');
    }
});

function closeDeleteLinkModal() {
    document.getElementById('deleteLinkConfirmModal').classList.add('hidden');
    document.body.style.overflow = 'auto';
}

function openLeaveConfirm() {
    document.getElementById('leaveConfirmModal').classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function closeLeaveConfirm() {
    document.getElementById('leaveConfirmModal').classList.add('hidden');
    document.body.style.overflow = 'auto';
}

const confirmKickBtn = document.getElementById('confirmKickBtn');
if (confirmKickBtn) {
    confirmKickBtn.addEventListener('click', async function() {
        if (!targetMemberId) return;

        const pathSegments = window.location.pathname.split('/');
        const groupId = pathSegments[pathSegments.indexOf('groups') + 1];

        try {
            const response = await fetch(`/api/groups/${groupId}/members/${targetMemberId}/kick`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const result = await response.json();

            if (response.ok) {
                showToast('success', result.message);
                setTimeout(() => { location.reload(); }, 2000);
            } else {
                showToast('error', result.message);
            }
        } catch (error) {
            console.error("Error kicking member:", error);
            showToast('error', '서버 통신 중 오류가 발생했습니다.');
        }
    });
}

function openInviteMemberModal() {
    document.getElementById('inviteMemberModal').classList.remove('hidden');
}

function closeInviteMemberModal() {
    document.getElementById('inviteMemberModal').classList.add('hidden');
}

let targetMemberId = null;
function openKickModal(memberId, memberName) {
    targetMemberId = memberId;
    document.getElementById('kickMemberName').innerText = memberName;
    document.getElementById('kickMemberModal').classList.remove('hidden');
}

function closeKickModal() {
    document.getElementById('kickMemberModal').classList.add('hidden');
}

function handleKickClick(button) {
    const id = button.getAttribute('data-id');
    const name = button.getAttribute('data-name');
    openKickModal(id, name);
}

let selectedMemberId = null;
function openTransferModal() {
    const modal = document.getElementById('transferOwnershipModal');
    if (modal) {
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }
}

function closeTransferModal() {
    const modal = document.getElementById('transferOwnershipModal');
    if (modal) {
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
    }
    selectedMemberId = null;
}

function handleTransferClick(button) {
    const id = button.getAttribute('data-id');
    const name = button.getAttribute('data-name');

    selectedMemberId = id;
    const nameSpan = document.getElementById('targetOwnerName');
    if (nameSpan) {
        nameSpan.innerText = name;
    }

    openTransferModal();
}

async function leaveGroup() {
    const pathSegments = window.location.pathname.split('/');
    const groupId = pathSegments[pathSegments.indexOf('groups') + 1];

    try {
        const response = await fetch(`/api/groups/${groupId}/members/leave`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const result = await response.json();

        if (response.ok) {
            showToast('success', result.message);
            setTimeout(() => {
                location.href = '/members/me/groups';
            }, 2000);
        } else {
            showToast('error', result.message);
        }
    } catch (error) {
        console.error("Error leaving group:", error);
        showToast('error', '서버와의 통신에 실패했습니다.');
    }
}

document.getElementById('confirmTransferBtn')?.addEventListener('click', async function() {
    if (!selectedMemberId) {
        showToast('error', '위임할 대상을 선택해주세요.');
        return;
    }

    const pathSegments = window.location.pathname.split('/');
    const groupId = pathSegments[pathSegments.indexOf('groups') + 1];

    try {
        const response = await fetch(`/api/groups/${groupId}/members/${selectedMemberId}/transfer`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
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
        console.error("Error transferring ownership:", error);
        showToast('error', '서버 통신 중 오류가 발생했습니다.');
    }
});

document.getElementById('confirmDeleteGroupBtn')?.addEventListener('click', async function() {
    const pathSegments = window.location.pathname.split('/');
    const groupId = pathSegments[pathSegments.indexOf('groups') + 1];

    try {
        const response = await fetch(`/api/groups/${groupId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const result = await response.json();

        if (response.ok) {
            showToast('success', result.message);
            setTimeout(() => {
                location.href = '/members/me/groups';
            }, 2000);
        } else {
            showToast('error', result.message);
        }
    } catch (error) {
        console.error("Error deleting group:", error);
        showToast('error', '서버 통신 중 오류가 발생했습니다.');
    }
});

function openDeleteConfirm() {
    const modal = document.getElementById('deleteConfirmModal');
    if (modal) {
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }
}

function closeDeleteConfirm() {
    const modal = document.getElementById('deleteConfirmModal');
    if (modal) {
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
    }
}

document.getElementById('inviteMemberForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();

    const memberLoginId = this.memberLoginId.value.trim();
    if (!memberLoginId) {
        showToast('error', '초대할 사용자의 아이디를 입력하세요.');
        return;
    }

    const pathSegments = window.location.pathname.split('/');
    const groupId = pathSegments[pathSegments.indexOf('groups') + 1];

    try {
        const response = await fetch(`/api/groups/${groupId}/invitations`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                memberId: memberLoginId
            })
        });

        const result = await response.json();

        if (response.ok) {
            showToast('success', result.message);
            setTimeout(() => { location.reload(); }, 1500);
        } else {
            showToast('error', result.message);
        }
    } catch (error) {
        console.error("Error inviting member:", error);
        showToast('error', '서버 통신 중 오류가 발생했습니다.');
    }
});

function initGroupCalendarConfig() {
    updateGroupCalendarHeader();
    loadGroupCalendar(groupCalendarYear, groupCalendarMonth);
    loadUpcomingSchedules();
    setupGroupCalendarNav();
}

function updateGroupCalendarHeader() {
    const headerTitle = document.querySelector('#calendar-view').parentElement.parentElement.querySelector('h3.text-xl.font-black');
    if (headerTitle) {
        headerTitle.innerText = `${groupCalendarYear}년 ${groupCalendarMonth}월`;
    }
}

function setupGroupCalendarNav() {
    const calendarWrapper = document.getElementById('calendar-view').parentElement.parentElement;
    if (!calendarWrapper) return;

    const prevBtn = calendarWrapper.querySelector('button:first-child');
    const nextBtn = calendarWrapper.querySelector('button:last-child');
    const todayBtn = calendarWrapper.querySelector('button.bg-gray-900');

    if (prevBtn && !prevBtn.dataset.bound) {
        prevBtn.dataset.bound = true;
        prevBtn.addEventListener('click', () => {
            groupCalendarMonth--;
            if (groupCalendarMonth < 1) {
                groupCalendarMonth = 12;
                groupCalendarYear--;
            }
            initGroupCalendarConfig();
        });
    }

    if (nextBtn && !nextBtn.dataset.bound) {
        nextBtn.dataset.bound = true;
        nextBtn.addEventListener('click', () => {
            groupCalendarMonth++;
            if (groupCalendarMonth > 12) {
                groupCalendarMonth = 1;
                groupCalendarYear++;
            }
            initGroupCalendarConfig();
        });
    }

    if (todayBtn && !todayBtn.dataset.bound) {
        todayBtn.dataset.bound = true;
        todayBtn.addEventListener('click', () => {
            groupCalendarYear = new Date().getFullYear();
            groupCalendarMonth = new Date().getMonth() + 1;
            initGroupCalendarConfig();
        });
    }
}

async function loadGroupCalendar(year, month) {
    const startDate = `${year}-${String(month).padStart(2, '0')}-01`;
    const lastDay = new Date(year, month, 0).getDate();
    const endDate = `${year}-${String(month).padStart(2, '0')}-${lastDay}`;

    try {
        const response = await fetch(`/api/groups/${currentGroupId}/schedules?startDate=${startDate}&endDate=${endDate}`);
        const json = await response.json();

        if (response.ok && json.data) {
            groupAllSchedulesCached = json.data;
            const countEl = document.getElementById('month-total-count');
            if (countEl) {
                countEl.innerText = groupAllSchedulesCached.length;
            }
            renderMinimalCalendar(year, month, json.data);
        }
    } catch (error) {
        console.error("Calendar Load Error:", error);
    }
}

function renderMinimalCalendar(year, month, schedules) {
    const container = document.getElementById('calendar-view');
    if (!container) return;
    container.innerHTML = '';

    const firstDayIndex = new Date(year, month - 1, 1).getDay();
    const lastDay = new Date(year, month, 0).getDate();

    for (let i = 0; i < firstDayIndex; i++) {
        const emptyCell = document.createElement('div');
        emptyCell.className = 'bg-gray-50/50 aspect-square';
        container.appendChild(emptyCell);
    }

    for (let day = 1; day <= lastDay; day++) {
        const currentDateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

        const daySchedule = schedules.find(s => {
            const start = s.startDateTime ? s.startDateTime.split('T')[0] : null;
            const end = s.endDateTime ? s.endDateTime.split('T')[0] : null;

            if (start && end) {
                return currentDateStr >= start && currentDateStr <= end;
            } else if (end) {
                return currentDateStr === end;
            } else if (start) {
                return currentDateStr === start;
            }
            return false;
        });

        const dayCell = document.createElement('div');
        dayCell.className = 'bg-white aspect-square p-2 hover:bg-gray-50 transition-all relative group cursor-pointer flex items-center justify-center';

        const dateSpan = document.createElement('span');
        dateSpan.innerText = day;
        dateSpan.className = 'absolute top-2 left-3 text-xs font-bold text-gray-400 group-hover:text-indigo-600';

        const currentDayOfWeek = new Date(year, month - 1, day).getDay();
        if (currentDayOfWeek === 0) dateSpan.classList.add('text-red-400');
        if (currentDayOfWeek === 6) dateSpan.classList.add('text-blue-400');

        if (daySchedule) {
            const baseColor = daySchedule.color;
            dayCell.className = `bg-white aspect-square p-2 transition-all relative group cursor-pointer flex items-center justify-center ${baseColor}/10 hover:${baseColor}/20`;
            dateSpan.className = 'absolute top-2 left-3 text-xs font-black';

            if (baseColor.includes('gray')) dateSpan.classList.add('text-gray-800');
            else if (baseColor.includes('indigo')) dateSpan.classList.add('text-indigo-600');
            else if (baseColor.includes('rose')) dateSpan.classList.add('text-rose-600');
            else if (baseColor.includes('emerald')) dateSpan.classList.add('text-emerald-600');
        }

        dayCell.appendChild(dateSpan);

        dayCell.addEventListener('click', () => {
            handleSideDayClick(currentDateStr);
        });

        container.appendChild(dayCell);
    }
}

async function loadUpcomingSchedules() {
    const container = document.getElementById('upcoming-schedule-list');
    if (!container) return;

    try {
        const response = await fetch(`/api/groups/${currentGroupId}/schedules/upcoming`);
        const json = await response.json();

        if (response.ok && json.data) {
            renderUpcomingSchedules(json.data);
        }
    } catch (error) {
        console.error("Upcoming Schedules Load Error:", error);
    }
}

function renderUpcomingSchedules(schedules) {
    const container = document.getElementById('upcoming-schedule-list');
    if (!container) return;
    container.innerHTML = '';

    if (schedules.length === 0) {
        container.innerHTML = `
            <div class="flex flex-col items-center justify-center py-12 text-center animate-in fade-in duration-500">
                <div class="w-16 h-16 bg-gray-50 rounded-2xl flex items-center justify-center text-gray-300 mb-3 border border-gray-100 shadow-inner">
                    <i class="fa-solid fa-calendar-xmark text-xl"></i>
                </div>
                <p class="text-xs font-bold text-gray-400">다가오는 일정이 없습니다</p>
                <p class="text-[10px] text-gray-300 mt-1">그룹원들과 새로운 일정을 계획해 보세요</p>
            </div>
        `;
        return;
    }

    const monthNames = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];

    schedules.forEach(s => {
        const startParts = s.startDateTime ? s.startDateTime.split('T') : null;
        const endParts = s.endDateTime ? s.endDateTime.split('T') : null;

        const targetDateStr = startParts ? startParts[0] : endParts[0];
        const dateObj = new Date(targetDateStr);
        const monthStr = monthNames[dateObj.getMonth()];
        const dayStr = dateObj.getDate();

        let timeStr = '하루 종일';

        if (startParts && endParts) {
            const startDateVal = startParts[0];
            const endDateVal = endParts[0];

            if (startDateVal !== endDateVal) {
                const sDate = new Date(startDateVal);
                const eDate = new Date(endDateVal);
                const sM = String(sDate.getMonth() + 1).padStart(2, '0');
                const sD = String(sDate.getDate()).padStart(2, '0');
                const eM = String(eDate.getMonth() + 1).padStart(2, '0');
                const eD = String(eDate.getDate()).padStart(2, '0');
                timeStr = `${sM}/${sD} ~ ${eM}/${eD}`;
            } else {
                const startTime = startParts[1].substring(0, 5);
                const endTime = endParts[1].substring(0, 5);
                timeStr = `${startTime} - ${endTime}`;
            }
        } else if (endParts) {
            const endTime = endParts[1].substring(0, 5);
            timeStr = `${endTime}까지`;
        }

        const baseColor = s.color ? s.color : 'bg-indigo-500';
        const lightBgClass = baseColor.replace('bg-', 'bg-') + '/10';
        const textColorClass = baseColor.replace('bg-', 'text-');

        const item = document.createElement('div');
        item.className = 'flex items-start gap-4 p-3 hover:bg-gray-50 rounded-2xl transition-all cursor-pointer group';

        if (typeof loadScheduleDetail === 'function') {
            item.setAttribute('onclick', `loadScheduleDetail(${s.id})`);
        }

        item.innerHTML = `
            <div class="w-12 h-12 rounded-xl ${lightBgClass} ${textColorClass} flex flex-col items-center justify-center shrink-0 border border-current/10">
                <span class="text-[10px] font-bold leading-none">${monthStr}</span>
                <span class="text-lg font-black leading-none mt-1">${dayStr}</span>
            </div>
            <div class="min-w-0 flex-1">
                <p class="text-sm font-bold text-gray-800 truncate group-hover:text-indigo-600 transition-colors">${s.title}</p>
                <p class="text-[11px] text-gray-400 mt-1 flex items-center gap-1">
                    <i class="fa-regular fa-clock"></i> ${timeStr}
                </p>
            </div>
        `;
        container.appendChild(item);
    });
}

let groupSelectedScheduleData = null;
let currentDeleteCommentId = null;

function loadScheduleDetail(scheduleId) {
    fetch(`/api/schedules/${scheduleId}`)
        .then(res => res.json())
        .then(result => {
            if (result.data) {
                const s = result.data;
                groupSelectedScheduleData = s;

                document.getElementById('detail-title').innerText = s.title;

                const contentEl = document.getElementById('detail-content');
                if (contentEl) {
                    if (s.content && s.content.trim() !== '') {
                        contentEl.innerText = s.content;
                        contentEl.closest('.p-8 > div').classList.remove('hidden');
                    } else {
                        contentEl.closest('.p-8 > div').classList.add('hidden');
                    }
                }

                const startRaw = s.startDateTime ? s.startDateTime.replace('T', ' ').substring(0, 16) : null;
                const endRaw = s.endDateTime ? s.endDateTime.replace('T', ' ').substring(0, 16) : null;

                let dateText = '';

                const formatAmPmTime = (dateTimeStr) => {
                    if (!dateTimeStr) return '';
                    const timePart = dateTimeStr.split(' ')[1];
                    if (!timePart) return '';
                    const [hourStr, minStr] = timePart.split(':');
                    const hours = parseInt(hourStr, 10);
                    const ampm = hours < 12 ? '오전' : '오후';
                    const displayHour = String(hours % 12 || 12).padStart(2, '0');
                    return `${ampm} ${displayHour}:${minStr}`;
                };

                const formatKoreanDate = (dateTimeStr) => {
                    if (!dateTimeStr) return '';
                    const datePart = dateTimeStr.split(' ')[1] ? dateTimeStr.split(' ')[0] : dateTimeStr;
                    const [yyyy, mm, dd] = datePart.split('-');
                    return `${parseInt(mm, 10)}월 ${parseInt(dd, 10)}일`;
                };

                const formatSlashDate = (dateTimeStr) => {
                    if (!dateTimeStr) return '';
                    const datePart = dateTimeStr.split(' ')[1] ? dateTimeStr.split(' ')[0] : dateTimeStr;
                    const [yyyy, mm, dd] = datePart.split('-');
                    return `${mm}/${dd}`;
                };

                if (startRaw && endRaw) {
                    const startDatePart = startRaw.split(' ')[0];
                    const endDatePart = endRaw.split(' ')[0];

                    if (startDatePart === endDatePart) {
                        const krDate = formatKoreanDate(startRaw);
                        const startTime = formatAmPmTime(startRaw);
                        const endTime = formatAmPmTime(endRaw);
                        dateText = `${krDate} ${startTime} ~ ${endTime}`;
                    } else {
                        const sDate = formatSlashDate(startRaw);
                        const eDate = formatSlashDate(endRaw);
                        dateText = `${sDate} ~ ${eDate}`;
                    }
                } else if (endRaw && !startRaw) {
                    const [yyyy, mm, dd] = endRaw.split(' ')[0].split('-');
                    const endTime = formatAmPmTime(endRaw);
                    dateText = `${mm}월 ${dd}일 ${endTime} 까지`;
                } else if (startRaw) {
                    const krDate = formatKoreanDate(startRaw);
                    const startTime = formatAmPmTime(startRaw);
                    dateText = `${krDate} ${startTime} 시작`;
                }

                document.getElementById('detail-datetime').innerText = dateText;

                const locEl = document.getElementById('detail-location');
                if (locEl) {
                    if (s.location && s.location.trim() !== '') {
                        locEl.innerText = s.location;
                        locEl.closest('.p-8 > div').classList.remove('hidden');
                    } else {
                        locEl.closest('.p-8 > div').classList.add('hidden');
                    }
                }

                const statusUncheck = document.getElementById('detail-status-uncheck');
                const statusChecked = document.getElementById('detail-status-checked');
                const statusText = document.getElementById('detail-status-text');
                if (statusUncheck && statusChecked && statusText) {
                    if (s.isCompleted === true) {
                        statusUncheck.classList.add('hidden');
                        statusChecked.classList.remove('hidden');
                        statusText.innerText = "완료";
                        statusText.className = "text-xs font-bold text-indigo-600 tracking-tight";
                    } else {
                        statusChecked.classList.add('hidden');
                        statusUncheck.classList.remove('hidden');
                        statusText.innerText = "진행 중";
                        statusText.className = "text-xs font-medium text-gray-950 tracking-tight";
                    }
                }

                const targetTypeBadge = document.getElementById('detail-targetType');
                if (targetTypeBadge) {
                    targetTypeBadge.innerText = "👥 그룹 일정";
                    targetTypeBadge.className = "inline-flex items-center gap-1 text-[10px] font-black text-indigo-600 bg-indigo-50 px-3 py-1 rounded-full uppercase tracking-tighter";
                }

                document.getElementById('detail-group-item')?.classList.remove('hidden');
                document.getElementById('detail-groupName')?.classList.remove('hidden');

                const detailGroupNameSpan = document.getElementById('detail-group-name') || document.getElementById('detail-groupName-text');
                if (detailGroupNameSpan) {
                    detailGroupNameSpan.innerText = s.groupName || "소속 그룹";
                }

                document.getElementById('detail-creator-name').innerText = s.profileName || "정보 없음";

                const commentContainer = document.getElementById('detail-comment-list');
                document.getElementById('detail-comment-count').innerText = s.comments ? s.comments.length : 0;

                const emptyStateBox = document.getElementById('comment-empty-state');

                if (!s.comments || s.comments.length === 0) {
                    emptyStateBox?.classList.remove('hidden');
                    if (commentContainer) commentContainer.innerHTML = '';
                } else {
                    emptyStateBox?.classList.add('hidden');
                    commentContainer.innerHTML = s.comments.map(c => {
                        const profileSrc = c.profileImage ? '/member/' + c.profileImage : '/images/default-profile.png';
                        const isOwnerGuard = (c.isOwner === true || c.memberId === s.memberId);

                        let dateRawStr = c.createdDate;
                        if (dateRawStr && typeof dateRawStr === 'string' && dateRawStr.includes('.')) {
                            dateRawStr = dateRawStr.split('.')[0];
                        }

                        const commentDate = new Date(dateRawStr);
                        const today = new Date();
                        let displayTimeLabel = '';

                        if (!isNaN(commentDate.getTime())) {
                            const isToday = commentDate.getFullYear() === today.getFullYear() &&
                                            commentDate.getMonth() === today.getMonth() &&
                                            commentDate.getDate() === today.getDate();

                            if (isToday) {
                                const hours = commentDate.getHours();
                                const minutes = String(commentDate.getMinutes()).padStart(2, '0');
                                const ampm = hours < 12 ? '오전' : '오후';
                                const displayHour = String(hours % 12 || 12).padStart(2, '0');
                                displayTimeLabel = `${ampm} ${displayHour}:${minutes}`;
                            } else {
                                const yyyy = commentDate.getFullYear();
                                const mm = String(commentDate.getMonth() + 1).padStart(2, '0');
                                const dd = String(commentDate.getDate()).padStart(2, '0');
                                displayTimeLabel = `${yyyy}-${mm}-${dd}`;
                            }
                        } else {
                            displayTimeLabel = c.createdDate ? c.createdDate.substring(0, 10) : '';
                        }

                        return `
                            <div id="comment-item-${c.id}" class="group p-5 bg-gray-50 border border-gray-100 rounded-[1.5rem] flex items-start gap-4 transition-all hover:bg-white hover:shadow-md">
                                <img src="${profileSrc}" class="w-9 h-9 rounded-full object-cover shrink-0 shadow-sm border border-gray-100">
                                <div class="flex-1 min-w-0 space-y-1.5">
                                    <div class="flex items-center justify-between">
                                        <div class="flex items-center gap-2">
                                            <span class="text-[12px] font-bold text-gray-900">${c.profileName}</span>
                                            <span class="text-[9px] font-medium text-gray-400 uppercase tracking-tight">${displayTimeLabel}</span>
                                        </div>
                                        <div class="opacity-0 group-hover:opacity-100 flex items-center gap-2 transition-all">
                                            ${isOwnerGuard ? `
                                                <button type="button" onclick="toggleEditCommentForm(${c.id})" class="text-gray-400 hover:text-indigo-500 transition-colors"><i class="fa-solid fa-pen text-[10px]"></i></button>
                                                <button type="button" onclick="openDeleteCommentModal(${c.id})" class="text-gray-400 hover:text-rose-500 transition-colors"><i class="fa-solid fa-trash-can text-[10px]"></i></button>
                                            ` : ''}
                                        </div>
                                    </div>
                                    <p id="comment-text-${c.id}" class="text-[12px] text-gray-600 font-medium leading-relaxed block">${c.content}</p>
                                    <div id="comment-edit-box-${c.id}" class="hidden mt-2 space-y-2">
                                        <textarea id="comment-edit-input-${c.id}" rows="2" class="w-full p-3 bg-white border border-gray-200 rounded-xl text-[12px] font-medium leading-relaxed focus:ring-1 focus:ring-black focus:outline-none transition-all resize-none">${c.content}</textarea>
                                        <div class="flex justify-end gap-1.5">
                                            <button type="button" onclick="toggleEditCommentForm(${c.id})" class="px-3 py-1.5 bg-gray-200 text-gray-600 text-[10px] font-bold rounded-lg hover:bg-gray-300 transition-colors">취소</button>
                                            <button type="button" onclick="saveCommentEdit(${c.id})" class="px-3 py-1.5 bg-gray-900 text-white text-[10px] font-bold rounded-lg hover:bg-black transition-colors">저장</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        `;
                    }).join('');
                }

                document.getElementById('detail-comment-section')?.classList.remove('hidden');

                document.body.style.overflow = 'hidden';
                document.getElementById('detail-event-modal').classList.remove('hidden');
                document.getElementById('detail-event-modal').classList.add('flex');
            }
        })
        .catch(err => console.error("Group Detail Load Error:", err));
}

function closeDetailEventModal() {
    const modal = document.getElementById('detail-event-modal');
    if (modal) {
        modal.classList.add('hidden');
        modal.classList.remove('flex');
        document.body.style.overflow = 'auto';
    }
}

document.getElementById('btn-comment-submit').onclick = async function() {
    const scheduleId = groupSelectedScheduleData?.id;
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
            setTimeout(() => { loadScheduleDetail(scheduleId); }, 1200);
        } else {
            showToast('error', result.message);
        }
    } catch (error) {
        console.error('Comment Error:', error);
    }
};

document.getElementById('comment-input').onkeydown = function(e) {
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        document.getElementById('btn-comment-submit').click();
    }
};

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
    const scheduleId = groupSelectedScheduleData?.id;
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
            setTimeout(() => { loadScheduleDetail(scheduleId); }, 1200);
        } else {
            showToast('error', result.message);
        }
    } catch (error) {
        console.error('Comment Edit Error:', error);
    }
}

function openDeleteCommentModal(commentId) {
    currentDeleteCommentId = commentId;
    const modal = document.getElementById('deleteCommentConfirmModal');
    if (modal) {
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }
}

function closeDeleteCommentModal() {
    const modal = document.getElementById('deleteCommentConfirmModal');
    if (modal) {
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
    }
}

async function executeCommentDeleteReal() {
    if (!currentDeleteCommentId || !groupSelectedScheduleData) return;
    try {
        const response = await fetch(`/api/schedules/${groupSelectedScheduleData.id}/comments/${currentDeleteCommentId}`, {
            method: 'DELETE'
        });
        const result = await response.json();
        if (response.ok) {
            showToast('success', result.message);
            closeDeleteCommentModal();
            setTimeout(() => { loadScheduleDetail(groupSelectedScheduleData.id); }, 1200);
        } else {
            showToast('error', result.message);
            closeDeleteCommentModal();
        }
    } catch (error) {
        console.error(error);
        closeDeleteCommentModal();
    } finally {
        currentDeleteCommentId = null;
    }
}

function openDeleteScheduleModal() {
    const modal = document.getElementById('deleteScheduleConfirmModal');
    if (modal) modal.classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function closeDeleteScheduleModal() {
    const modal = document.getElementById('deleteScheduleConfirmModal');
    if (modal) modal.classList.add('hidden');
    document.body.style.overflow = 'auto';
}

async function executeScheduleDelete() {
    const id = groupSelectedScheduleData?.id;
    if (!id) return;
    const res = await fetch(`/api/schedules/${id}`, { method: 'DELETE' });
    const result = await res.json();
    if (res.ok) {
        showToast('success', result.message);
        setTimeout(() => { location.reload(); }, 1000);
    }
}

function handleGroupEditTrigger() {
    const s = groupSelectedScheduleData;
    if (!s) return;
    closeDetailEventModal();

    document.body.style.overflow = 'hidden';

    document.getElementById('modal-mode-title').innerText = "일정 정보 수정";
    document.getElementById('modal-mode-desc').innerText = "기존 일정의 세부 내용을 변경합니다.";

    document.getElementById('edit-mode-header')?.classList.add('hidden');

    document.getElementById('form-title').value = s.title || '';
    document.getElementById('form-content').value = s.content || '';
    document.getElementById('form-location').value = s.location || '';

    if (s.startDateTime) {
        const parts = s.startDateTime.split('T');
        document.getElementById('startDate').value = parts[0];
        if (parts[1]) document.getElementById('startTime').value = parts[1].substring(0, 5);
    }
    if (s.endDateTime) {
        const parts = s.endDateTime.split('T');
        document.getElementById('endDate').value = parts[0];
        if (parts[1]) document.getElementById('endTime').value = parts[1].substring(0, 5);
    }

    const radio = document.querySelector(`input[name="color"][value="${s.color}"]`);
    if (radio) radio.checked = true;

    document.getElementById('form-isCompleted').checked = s.isCompleted === true;
    document.getElementById('modal-status-checkbox-container').classList.remove('hidden');

    const form = document.getElementById('event-form');
    form.dataset.mode = 'edit';
    form.dataset.scheduleId = s.id;

    const modal = document.getElementById('event-modal');
    if (modal) {
        modal.classList.remove('hidden');
        modal.classList.add('flex');
    }
}

function closeAddEventModal() {
    const modal = document.getElementById('event-modal');
    if (modal) {
        modal.classList.add('hidden');
        modal.classList.remove('flex');
        document.body.style.overflow = 'auto';
        document.getElementById('event-form').reset();
        document.getElementById('edit-mode-header')?.classList.add('hidden');
    }
}

function handleSideDayClick(dateStr) {
    const sideBox = document.getElementById('selected-date-schedule-box');
    const titleSpan = document.getElementById('side-calendar-title');
    const container = document.getElementById('selected-date-schedule-list');

    if (!sideBox || !container) return;

    const [yyyy, mm, dd] = dateStr.split('-');
    if (titleSpan) {
        titleSpan.innerText = `${yyyy}년 ${mm}월 ${dd}일`;
        titleSpan.className = "text-sm font-black text-gray-900";
    }

    const daySchedules = groupAllSchedulesCached.filter(s => {
        const start = s.startDateTime ? s.startDateTime.split('T')[0] : null;
        const end = s.endDateTime ? s.endDateTime.split('T')[0] : null;
        if (start && end) {
            return dateStr >= start && dateStr <= end;
        } else if (end) {
            return dateStr === end;
        } else if (start) {
            return dateStr === start;
        }
        return false;
    });

    container.innerHTML = '';

    if (daySchedules.length === 0) {
        container.innerHTML = `
            <div class="flex flex-col items-center justify-center py-8 text-center bg-gray-50/50 border border-dashed border-gray-200 rounded-2xl">
                <div class="w-10 h-10 bg-white rounded-full flex items-center justify-center text-gray-300 mb-2 border border-gray-100 shadow-inner">
                    <i class="fa-solid fa-calendar-xmark text-xs"></i>
                </div>
                <p class="text-[11px] font-bold text-gray-400">등록된 일정이 없습니다</p>
                <p class="text-[10px] text-gray-300/80 mt-0.5">그날은 한적하고 자유로운 하루네요!</p>
            </div>
        `;
    } else {
        daySchedules.forEach(s => {
            const startParts = s.startDateTime ? s.startDateTime.split('T') : null;
            const endParts = s.endDateTime ? s.endDateTime.split('T') : null;

            let timeStr = '하루 종일';

            if (startParts && endParts) {
                const startDateVal = startParts[0];
                const endDateVal = endParts[0];

                if (startDateVal !== endDateVal) {
                    const sDate = new Date(startDateVal);
                    const eDate = new Date(endDateVal);
                    const sM = String(sDate.getMonth() + 1).padStart(2, '0');
                    const sD = String(sDate.getDate()).padStart(2, '0');
                    const eM = String(eDate.getMonth() + 1).padStart(2, '0');
                    const eD = String(eDate.getDate()).padStart(2, '0');
                    timeStr = `${sM}/${sD} ~ ${eM}/${eD}`;
                } else {
                    const startTime = startParts[1].substring(0, 5);
                    const endTime = endParts[1].substring(0, 5);
                    timeStr = `${startTime} - ${endTime}`;
                }
            } else if (endParts) {
                const endTime = endParts[1].substring(0, 5);
                timeStr = `${endTime}까지`;
            }

            const item = document.createElement('div');
            item.className = 'p-4 bg-gray-50 border border-gray-100 rounded-2xl hover:bg-gray-100/70 transition-all cursor-pointer group animate-in fade-in slide-in-from-bottom-1 duration-200';
            item.setAttribute('onclick', `loadScheduleDetail(${s.id})`);

            item.innerHTML = `
                <div class="min-w-0 flex-1">
                    <p class="text-sm font-bold text-gray-800 truncate group-hover:text-indigo-600 transition-colors">${s.title}</p>
                    <p class="text-[11px] text-gray-400 mt-1.5 flex items-center gap-1">
                        <i class="fa-regular fa-clock"></i> ${timeStr}
                    </p>
                </div>
            `;
            container.appendChild(item);
        });
    }

    sideBox.classList.remove('hidden');
}

document.getElementById('event-form').onsubmit = async function(e) {
    e.preventDefault();

    const formMode = this.dataset.mode;
    const scheduleId = this.dataset.scheduleId;

    const title = document.getElementById('form-title').value;
    const content = document.getElementById('form-content').value;
    const locationValue = document.getElementById('form-location').value;
    const startDate = document.getElementById('startDate').value;
    const startTime = document.getElementById('startTime').value;
    const endDate = document.getElementById('endDate').value;
    const endTime = document.getElementById('endTime').value;

    const checkedColorEl = this.querySelector('input[name="color"]:checked');
    if (!checkedColorEl) {
        if (typeof showToast === 'function') showToast('error', '배경색을 선택해 주세요.');
        return;
    }
    const color = checkedColorEl.value;

    if (!title.trim()) {
        showToast('error', '일정명을 입력해주세요.');
        return;
    }

    const combineDateTime = (dateVal, timeVal) => {
        if (!dateVal) return null;
        return timeVal ? `${dateVal}T${timeVal}` : dateVal;
    };

    const requestData = {
        groupId: currentGroupId,
        targetType: 'GROUP',
        title: title,
        content: content,
        location: locationValue ? locationValue : null,
        startDateTime: combineDateTime(startDate, startTime),
        endDateTime: combineDateTime(endDate, endTime),
        color: color,
        isCompleted: formMode === 'edit' ? document.getElementById('form-isCompleted').checked : false
    };

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
            setTimeout(() => { location.reload(); }, 2000);
        } else {
            showToast('error', result.message);
        }
    } catch (error) {
        console.error('Save Error:', error);
    }
};