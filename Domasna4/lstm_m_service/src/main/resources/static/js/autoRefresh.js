let intervalId;

async function checkFlagStatus() {
    try {

        if (localStorage.getItem('refreshed') === 'true') {
            return;
        }

        const response = await fetch('/fund-flag');
        const isFlagTrue = await response.json();

        if (!isFlagTrue) {

            clearInterval(intervalId);

            localStorage.setItem('refreshed', 'true');

            window.location.reload();
        }
    } catch (error) {
        console.error('Error checking flag status:', error);
    }
}

function resetRefreshedFlag() {
    fetch('/fund-flag')
        .then(response => response.json())
        .then(flagStatus => {
            if (flagStatus === true) {
                localStorage.removeItem('refreshed');
            }
        })
        .catch(error => console.error('Error checking flag status during page load:', error));
}

resetRefreshedFlag();

intervalId = setInterval(checkFlagStatus, 500);
