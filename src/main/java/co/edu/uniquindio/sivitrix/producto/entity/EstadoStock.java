package co.edu.uniquindio.sivitrix.producto.entity;

/** Estado del stock de un producto (SWR-12). Coincide con la clasificacion de las alertas. */
public enum EstadoStock {
    NORMAL,
    BAJO_STOCK,
    CRITICO;

    public static EstadoStock de(int stockActual, int stockMinimo) {
        if (stockActual > stockMinimo) {
            return NORMAL;
        }
        return stockActual == 0 ? CRITICO : BAJO_STOCK;
    }
}