// Const.js

export const API_PREFIX = "http://localhost:8080/api";
export const regionsData = [];
export const fetchStatus = {
    loading: false,
    error: null,
};

export const fetchRegionsData = async () => {
    // Check if data is already fetched
    if (regionsData.length > 0) {
        console.log("Data already fetched:", regionsData);
        return;
    }

    // Check if data is currently being fetched
    if (fetchStatus.loading) {
        console.log("Data is currently being fetched...");
        return;
    }

    // Set loading to true before starting the fetch
    fetchStatus.loading = true;

    try {
        const response = await fetch(`${API_PREFIX}/location/code`);
        const data = await response.json();
        regionsData.splice(0, regionsData.length, ...data);
        console.log("Data fetched:", regionsData);
    } catch (error) {
        fetchStatus.error = error;
        console.error('Error fetching data:', error);
    } finally {
        // Set loading to false after fetch completes (whether successful or not)
        fetchStatus.loading = false;
    }
};
