package com.example.proyectoecommerdsm;

public class pojo {
    public String getArticulos() {
        return Articulos;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getPrecio() {
        return Precio;
    }

    public void setPrecio(String precio) {
        Precio = precio;
    }

    String Descripcion;
    String Precio;
    String Cantidad;

    public String getCantidad() {
        return Cantidad;
    }

    public void setCantidad(String cantidad) {
        Cantidad = cantidad;
    }



    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    private int ID;
    public void setArticulos(String articulos) {
        Articulos = articulos;
    }

    private String Articulos;

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String imagen) {
        Imagen = imagen;
    }

    private String Imagen;

    public pojo() {

    }

}
