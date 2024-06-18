package com.busquedaLibros.desafioAlura.principal;

import com.busquedaLibros.desafioAlura.model.Datos;
import com.busquedaLibros.desafioAlura.model.DatosLibros;
import com.busquedaLibros.desafioAlura.service.ConsumoAPI;
import com.busquedaLibros.desafioAlura.service.ConvierteDatos;
import com.busquedaLibros.desafioAlura.service.IConvertirDatos;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner sc = new Scanner(System.in);

    public void mostrarMenu(){
        var json = consumoAPI.obtenerDatos(URL_BASE);
        System.out.println(json);

        var datos = conversor.obtenerDatos(json,Datos.class);
        System.out.println(datos);

        //Top 10 de libros más descargados
        System.out.println("Top 10 de libros más descargados");
        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDescargas).reversed())
                .limit(10)
                .map(l -> l.titulo().toUpperCase())
                .forEach(System.out::println);


        //Busqueda de libros por nombre
        System.out.println("Introduzca el nombre del libro que desea buscar: ");
        var tituloLibro = sc.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json,Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l ->l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()) {
            System.out.println("Libro encontrado");
            System.out.println(libroBuscado.get());
        }else {
            System.out.println("Erro Libro no encontado");
        }

        //Trabajando con estadisticas
        DoubleSummaryStatistics est = datos.resultados().stream()
                .filter(d -> d.numeroDescargas() >0 )
                .collect(Collectors.summarizingDouble(DatosLibros::numeroDescargas));

        System.out.println("Cantidad media de descargas: " +est.getAverage());
        System.out.println("Cantidad máxima de de descargas: " +est.getMax());
        System.out.println("Cantidad minima de descargas: " +est.getMin());
        System.out.println("Cantidad de registros evaluados para calcular las estadisticas: " +est.getCount());
    }
}
