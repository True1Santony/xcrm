import { renderVentasChart, renderInteraccionesChart, renderVentasPorCampaniaChart } from './handlers.js';

document.addEventListener('DOMContentLoaded', () => {
    renderVentasChart('ventasComercialChart');
    renderInteraccionesChart('interaccionesPorVentaChart');
    renderVentasPorCampaniaChart('ventasPorCampaniaChart');
});