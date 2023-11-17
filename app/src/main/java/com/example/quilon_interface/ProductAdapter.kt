package com.example.quilon_interface

import Produto
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(private val productList: List<Produto>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // ViewHolder para cada item na lista
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.idTitulo1)
        val priceTextView: TextView = itemView.findViewById(R.id.idPreco1)
        // Adicione mais elementos conforme necessário
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item_layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        // Configurar os dados para exibição
        holder.titleTextView.text = product.title
        holder.priceTextView.text = product.price.toString()
        // Configurar outros elementos conforme necessário
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
