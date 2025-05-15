import { fetchData } from './helpers.js';

export async function renderVentasChart(ctxId) {
    try {
        const data = await fetchData('/api/ventas-por-comercial');

        const labels = data.map(d => d.comercial);
        const values = data.map(d => Number(d.total_ventas) || 0);

        const ctx = document.getElementById(ctxId).getContext('2d');

        const backgroundColors = [
                    'rgba(54, 162, 235, 0.7)',
                    'rgba(255, 99, 132, 0.7)',
                    'rgba(255, 206, 86, 0.7)',
                    'rgba(75, 192, 192, 0.7)',
                    'rgba(153, 102, 255, 0.7)',
                    'rgba(255, 159, 64, 0.7)'
                ];

        const borderColors = backgroundColors.map(color => color.replace('0.7', '1'));

        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Total Ventas',
                    data: values,
                    backgroundColor: backgroundColors,
                    borderColor: borderColors,
                    borderWidth: 2,
                    hoverOffset: 10
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '40%',
                animation: {
                    duration: 1200,
                    easing: 'easeOutBounce',
                },
                plugins: {
                    legend: {
                        display: true,
                        position: 'left',
                        labels: {
                            font: {
                                family: 'Roboto',
                                size: 14
                            },
                            boxWidth: 20,
                            padding: 20,
                            color: '#333'
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0,0,0,0.7)',
                        titleFont: { size: 14, weight: 'bold' },
                        bodyFont: { size: 13 },
                        callbacks: {
                            label: ctx => `${ctx.label}: ${ctx.parsed} ventas`,
                        }
                    }
                }
            }
        });
    } catch (err) {
        console.error("Error al renderizar el gráfico:", err);
    }
}


export async function renderInteraccionesChart(ctxId) {
    try {
        const data = await fetchData('/api/interacciones-por-venta');

        // Agrupar datos por comercial para obtener colores únicos
        const comercialesSet = [...new Set(data.map(d => d.comercial))];

        const colors = {};
        comercialesSet.forEach((comercial, index) => {
            const hue = (index * 30) % 360; // Diferentes tonos
            colors[comercial] = `hsl(${hue}, 70%, 55%)`;
        });

        // Preparar datos para Chart.js
        const labels = data.map(d => d.venta);
        const backgroundColors = data.map(d => colors[d.comercial]);
        const borderColors = backgroundColors;

        const ctx = document.getElementById(ctxId).getContext('2d');

        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Interacciones por Venta',
                    data: data.map(d => Number(d.interacciones) || 0),
                    backgroundColor: backgroundColors,
                    borderColor: borderColors,
                    borderWidth: 1,
                    borderRadius: 4,
                    barPercentage: 0.8,
                    categoryPercentage: 0.9
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                indexAxis: 'x', // Barras verticales
                plugins: {
                    legend: {
                        display: true,
                        position: 'right',
                        labels: {
                            usePointStyle: true,
                            pointStyle: 'rectRounded',
                            font: {
                                family: 'Roboto',
                                size: 13
                            },
                            generateLabels: chart => {
                                return comercialesSet.map(comercial => ({
                                    text: comercial,
                                    fillStyle: colors[comercial],
                                    strokeStyle: colors[comercial],
                                    lineWidth: 1,
                                    hidden: false,
                                    pointStyle: 'rectRounded'
                                }));
                            }
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: ctx => `Interacciones: ${ctx.parsed.y}`
                        }
                    }
                },
                scales: {
                    x: {
                        ticks: {
                            autoSkip: false,
                            maxRotation: 45,
                            minRotation: 45,
                            font: { family: 'Roboto', size: 10 }
                        }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: {
                            font: { family: 'Roboto', size: 12 }
                        }
                    }
                }
            }
        });
    } catch (err) {
        console.error("Error al renderizar Interacciones por Venta:", err);
    }
}