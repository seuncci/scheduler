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