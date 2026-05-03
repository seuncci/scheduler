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

function openCreateLinkModal() { document.getElementById('createLinkModal').classList.remove('hidden'); document.body.style.overflow = 'hidden'; }
function closeCreateLinkModal() { document.getElementById('createLinkModal').classList.add('hidden'); document.body.style.overflow = 'auto'; }

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

document.getElementById('confirmKickBtn').addEventListener('click', async function() {
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
    document.getElementById('transferOwnershipModal').classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function closeTransferModal() {
    document.getElementById('transferOwnershipModal').classList.add('hidden');
    document.body.style.overflow = 'auto';
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