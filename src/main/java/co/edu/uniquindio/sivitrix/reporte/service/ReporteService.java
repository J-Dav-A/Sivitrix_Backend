package co.edu.uniquindio.sivitrix.reporte.service;

import co.edu.uniquindio.sivitrix.reporte.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {

    ReporteVentasResponse ventas(LocalDate desde, LocalDate hasta);

    List<ProductoVendidoResponse> productosMasVendidos(LocalDate desde, LocalDate hasta, int limite);

    List<ClienteCompraResponse> mejoresClientes(LocalDate desde, LocalDate hasta, int limite);

    ReporteInventarioResponse inventario();
}
