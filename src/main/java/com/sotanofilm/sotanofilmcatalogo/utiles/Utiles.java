package com.sotanofilm.sotanofilmcatalogo.utiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utiles {

    private Utiles() {

    }

    public static boolean esRutaPeliculaValida (String ruta) {
        boolean resultado = false;
        Path rutaPath = Paths.get(ruta);
        if (Files.exists(rutaPath) && !Files.isDirectory(rutaPath)
                && esArchivoContenedor(rutaPath))
            resultado = true;

        return resultado;
    }

    public static boolean esRutaPeliculaValida (Path ruta) {
        boolean resultado = false;
        if (Files.exists(ruta) && !Files.isDirectory(ruta)
                && esArchivoContenedor(ruta))
            resultado = true;

        return resultado;
    }

    public static boolean esArchivoContenedor (Path ruta) {
        boolean resultado = false;
        String nombreArchivo = ruta.getFileName().toString();
        if (nombreArchivo.contains("mkv") || nombreArchivo.contains("mp4") || nombreArchivo.contains("avi")) {
            resultado = true;
        }
        return resultado;
    }

    public static Integer obtenerDuracionEnMinutos (String duracion) {
        int resultado = 0;
        try {
            if (Objects.nonNull(duracion) && !duracion.isEmpty()) {
                LocalTime time = LocalTime.parse(duracion);
                int segundos = time.toSecondOfDay();
                float minutos = segundos/60F;
                resultado = Math.round(minutos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public static Float obtenerBitRateEnKB (String bytes) {
        float resultado = 0;
        try {
            if (Objects.nonNull(bytes) && !bytes.isEmpty()) {
                float bytesFloat = Float.parseFloat(bytes);
                resultado = bytesFloat/1024F;
                resultado = Math.round(resultado);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public static Integer obtenerTamanoEnKB (String bytes) {
        int resultado = 0;
        try {
            if (Objects.nonNull(bytes) && !bytes.isEmpty()) {
                float bytesFloat = Float.parseFloat(bytes);
                float resultadoFloat = bytesFloat/1024F;
                resultado = (int) resultadoFloat;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public static boolean contieneOtrosDirectorios (Path ruta) throws Exception {
        boolean resultado = false;
        try (Stream<Path> stream = Files.list(ruta)) {
            List<Path> subRutas = stream.collect(Collectors.toList());
            for (Path subRuta : subRutas) {
                if (Files.isDirectory(subRuta)) {
                    resultado = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    /**
     * Valida que un directorio sea valido
     *
     * @param directorio
     * @throws IllegalArgumentException
     */
    public static void validarDirectorio(Path directorio) throws Exception {
        if (directorio == null) {
            throw new IllegalArgumentException("La ruta no puede ser null.");
        }
        if (!Files.isDirectory(directorio)) {
            throw new IllegalArgumentException("La ruta indicada no corresponde a una carpeta.");
        }
    }
}
