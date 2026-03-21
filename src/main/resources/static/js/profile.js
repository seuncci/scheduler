document.addEventListener("DOMContentLoaded", function () {
    fetchProfileData();
});

async function fetchProfileData(callback) {
    try {
        const response = await fetch('/api/members/me', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        const result = await response.json();

        if (response.ok) {

            const member = result.data;
            document.getElementById('profile-name').textContent = member.name;
            document.getElementById('profile-email').textContent = member.email;

            if (member.profileImage) {
                document.getElementById('profile-img').src = "/member/" + member.profileImage;
            } else {
                document.getElementById('profile-img').src = "/images/" + "default-profile.png";
            }

            if (callback && typeof callback === 'function') {
                callback(member);
            }

        } else {

                alert(result.message);
        }
    } catch (error) {
        console.error("Error fetching profile:", error);
        alert("서버와 통신 중 오류가 발생했습니다.");
    }
}

function initImagePreview() {
    const fileInput = document.getElementById('edit-image');
    const previewImg = document.getElementById('form-img-preview');
    newBadge = document.getElementById('form-img-new-badge');

    if (!fileInput) return;

    fileInput.addEventListener('change', function(e) {
        const file = e.target.files[0];
        if (file) {

            if (!file.type.startsWith('image/')) {
                alert('이미지 파일만 선택할 수 있습니다.');
                this.value = '';
                return;
            }

            const reader = new FileReader();
            reader.onload = function(event) {

                if (previewImg) previewImg.src = event.target.result;

                if (newBadge) {
                    newBadge.classList.remove('hidden');
                }
            };
            reader.readAsDataURL(file);
        }
    });
}

function showToast(type, message) {
    const toast = document.getElementById('toast');
    const container = document.getElementById('toast-container');
    const icon = document.getElementById('toast-icon');
    const messageSpan = document.getElementById('toast-message');

    if (!toast || !container) return;

    container.classList.remove('bg-red-50', 'border-red-200', 'text-red-500', 'bg-green-50', 'border-green-200', 'text-green-500');
    icon.classList.remove('fa-circle-exclamation', 'fa-circle-check');

    if (type === 'success') {
        container.classList.add('bg-green-50', 'border-green-200', 'text-green-600');
        icon.classList.add('fa-circle-check');
    } else {
        container.classList.add('bg-red-50', 'border-red-200', 'text-red-600');
        icon.classList.add('fa-circle-exclamation');
    }

    messageSpan.textContent = message;

    setTimeout(() => { toast.style.top = '20px'; }, 100);
    setTimeout(() => { toast.style.top = '-100px'; }, 2000);
}

async function handleProfileUpdate(e) {
    e.preventDefault();

    const name = document.getElementById('edit-name').value;
    const email = document.getElementById('edit-email').value;
    const password = document.getElementById('edit-password').value;
    const passwordConfirm = document.getElementById('edit-passwordConfirm').value;
    const fileInput = document.getElementById('edit-image');

    if (password && password !== passwordConfirm) {
        showToast('error', '비밀번호가 일치하지 않습니다.');
        return;
    }

    const formData = new FormData();

    const updateData = {
        name: name,
        email: email,
        password: password || null,
        passwordConfirm: passwordConfirm || null
    };

    formData.append('data', new Blob([JSON.stringify(updateData)], {
        type: 'application/json'
    }));

    if (fileInput.files.length > 0) {
        formData.append('image', fileInput.files[0]);
    }

    try {
        const response = await fetch('/api/members/me', {
            method: 'PATCH',
            body: formData
        });

        const result = await response.json();

        if (response.ok) {
            showToast('success', result.message);

            setTimeout(() => {
                location.href = "/members/me";
            }, 2000);

        } else {

            showToast('error', result.message);
        }
    } catch (error) {
        console.error("Error updating profile:", error);
        alert("서버 통신 중 오류가 발생했습니다.");
    }
}