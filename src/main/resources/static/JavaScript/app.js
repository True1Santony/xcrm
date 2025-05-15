import { renderVentasChart, renderInteraccionesChart } from './handlers.js';

document.addEventListener('DOMContentLoaded', () => {
    renderVentasChart('ventasComercialChart');
    renderInteraccionesChart('interaccionesPorVentaChart');
});