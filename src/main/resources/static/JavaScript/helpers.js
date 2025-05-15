export async function fetchData(url) {
    const response = await fetch(url);
    if (!response.ok) throw new Error("Error al obtener los datos");
    return await response.json();
}