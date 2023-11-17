package com.example.quilon_interface

import Produto
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class EdicaoProdutoActivity : AppCompatActivity() {

    private val apiService = Conexao().createApiService()

    private var imageViewIndex = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edicao_produto)

//  <<<<<<<<<<<<<<<<<<   Botão Voltar  >>>>>>>>>>>>>>>>>>
        val btnVoltar: ImageButton = findViewById(R.id.Voltar)

        btnVoltar.setOnClickListener {
            val exibeProdutoIntent = Intent(this, ExibicaoProdutoActivity::class.java)
            startActivity(exibeProdutoIntent)
        }

//  <<<<<<<<<<<<<<<<<<   Buscar dados no banco  >>>>>>>>>>>>>>>>>>
        val produtoId = 4 // Substitua pelo ID do produto desejado
        val call = apiService.receberProduto(produtoId)

        call.enqueue(object : Callback<Produto> {
            override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                if (response.isSuccessful) {
                    val produto = response.body()

                    // Log para verificar se os dados do produto estão corretos
                    Toast.makeText(applicationContext, "Produto recebido: $produto", Toast.LENGTH_SHORT).show()

                    // Obtenha referências ao contexto da activity para usar nas atualizações
                    val context = this@EdicaoProdutoActivity

                    //  <<<<<<<<<<<<<<<<<<   Título  >>>>>>>>>>>>>>>>>>
                    val txtTitulo1: EditText = findViewById(R.id.txt_titulo1)
                    txtTitulo1.setText(produto?.title)

                    //  <<<<<<<<<<<<<<<<<<   Categoria  >>>>>>>>>>>>>>>>>>
                    val spinnerTipo: Spinner = findViewById(R.id.spinnerTipo)
                    val adapterTipo = ArrayAdapter.createFromResource(
                        context,
                        R.array.tipos,
                        R.layout.spinner_item_layout
                    )
                    adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerTipo.adapter = adapterTipo
                    val posicaoTipo = adapterTipo.getPosition(produto?.category)
                    spinnerTipo.setSelection(posicaoTipo)

                    //  <<<<<<<<<<<<<<<<<<   Descrição  >>>>>>>>>>>>>>>>>>
                    val txtDescricao: EditText = findViewById(R.id.txt_descricao)
                    txtDescricao.setText(produto?.description)

                    //  <<<<<<<<<<<<<<<<<<   Tempo de Produção  >>>>>>>>>>>>>>>>>>
                    val spinnerPrazo: Spinner = findViewById(R.id.spinnerPrazo)
                    val adapterPrazo = ArrayAdapter.createFromResource(
                        context,
                        R.array.prazo,
                        R.layout.spinner_item_layout
                    )
                    adapterPrazo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerPrazo.adapter = adapterPrazo
                    val posicaoPrazo = adapterPrazo.getPosition(produto?.production_time)
                    spinnerPrazo.setSelection(posicaoPrazo)

                    // <<<<<<<<<<<<<<<<<<< Preço >>>>>>>>>>>>>>>>>>>
                    val txtPreco: EditText = findViewById(R.id.txt_preco)
                    txtPreco.setText(produto?.price.toString())

                    // <<<<<<<<<<<<<<<<<<< Estoque >>>>>>>>>>>>>>>>>>>
                    val txtEstoque: EditText = findViewById(R.id.txt_estoque)
                    txtEstoque.setText(produto?.stock.toString())


                } else {
                    // Trate o caso em que a resposta não foi bem-sucedida
                    Toast.makeText(applicationContext, "Erro ao receber dados do produto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Produto>, t: Throwable) {
                // Trate a falha na requisição
                Toast.makeText(applicationContext, "Falha na requisição: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

//  <<<<<<<<<<<<<<<<<<   Botão Adicionar Foto  >>>>>>>>>>>>>>>>>>
        val adicionarFotoButton = findViewById<ImageButton>(R.id.adicionar_foto)
        adicionarFotoButton.setOnClickListener { openImagePicker() }

//  <<<<<<<<<<<<<<<<<<   Imagem 1  >>>>>>>>>>>>>>>>>>
        val imageView1: ImageView = findViewById(R.id.imagem1)
        imageView1.setImageResource(R.drawable.image_artesanato)

//  <<<<<<<<<<<<<<<<<<   Imagem 2  >>>>>>>>>>>>>>>>>>
        val imageView2: ImageView = findViewById(R.id.imagem2)
        imageView2.setImageResource(R.drawable.image_artesanato)

//  <<<<<<<<<<<<<<<<<<   Imagem 3  >>>>>>>>>>>>>>>>>>
        val imageView3: ImageView = findViewById(R.id.imagem3)
        imageView3.setImageResource(R.drawable.image_artesanato)

//  <<<<<<<<<<<<<<<<<<   Botão Salvar  >>>>>>>>>>>>>>>>>>
        val btnSalvar: ImageButton = findViewById(R.id.btn_salvar)

        btnSalvar.setOnClickListener {

            // Obtém os dados atualizados da tela
            val novoTitulo = findViewById<EditText>(R.id.txt_titulo1).text.toString()
            val novaCategoria = findViewById<Spinner>(R.id.spinnerTipo).selectedItem.toString()
            val novaDescricao = findViewById<EditText>(R.id.txt_descricao).text.toString()
            val novoPrazo = findViewById<Spinner>(R.id.spinnerPrazo).selectedItem.toString()
            val novoPreco = findViewById<EditText>(R.id.txt_preco).text.toString()
            val novoEstoque = findViewById<EditText>(R.id.txt_estoque).text.toString()

            // Monta um objeto Produto com os dados atualizados
            val produtoAtualizado = Produto(
                novoTitulo,
                novaCategoria,
                novaDescricao,
                novoPrazo,
                novoPreco,
                novoEstoque
            )

            // Chama a API para atualizar os dados no banco
            val call = apiService.atualizarProduto(produtoId, produtoAtualizado)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        // Trate o caso em que a atualização foi bem-sucedida
                        Toast.makeText(applicationContext, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show()
                        abrirInterfaceExibicaoProduto()
                    } else {
                        // Trate o caso em que a resposta não foi bem-sucedida
                        Toast.makeText(applicationContext, "Erro ao atualizar dados", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Trate a falha na requisição
                    Toast.makeText(applicationContext, "Falha na requisição: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

//  <<<<<<<<<<<<<<<<<<   Botão Deletar Produto  >>>>>>>>>>>>>>>>>>
        val btnDeletarProduto: ImageButton = findViewById(R.id.btn_deletar)

        btnDeletarProduto.setOnClickListener {

            // Chama a API para excluir o produto do banco
            val call = apiService.deletarProduto(produtoId)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        // Em que a exclusão foi bem-sucedida
                        Toast.makeText(applicationContext, "Produto excluído com sucesso", Toast.LENGTH_SHORT).show()

                        // Redireciona para a tela de exibição de produtos
                        abrirInterfaceExibicaoProduto()
                    } else {
                        // Em que a resposta não foi bem-sucedida
                        Toast.makeText(applicationContext, "Erro ao excluir produto", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(applicationContext, "Falha na requisição: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }
    //  <<<<<<<<<<<<<<<<<<   Função para abrir a Interface de Exibição  >>>>>>>>>>>>>>>>>>
    fun abrirInterfaceExibicaoProduto() {
        val exibeProdutoIntent = Intent(this, ExibicaoProdutoActivity::class.java)
        startActivity(exibeProdutoIntent)
    }

    //  <<<<<<<<<<<<<<<<<<   Funções para Adicionar Foto  >>>>>>>>>>>>>>>>>>
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            if (data != null) {
                val selectedImageUri = data.data
                if (selectedImageUri != null) {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            selectedImageUri
                        )
                        val imageView: ImageView? = getNextImageView()
                        imageView?.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //  <<<<<<<<<<<<<<<<<<   Função para Adicionar Foto na sequência >>>>>>>>>>>>>>>>>>
    @SuppressLint("WrongViewCast")
    private fun getNextImageView(): ImageView? {
        when (imageViewIndex) {
            1 -> {
                imageViewIndex++
                return findViewById(R.id.imagem1)
            }
            2 -> {
                imageViewIndex++
                return findViewById(R.id.imagem2)
            }
            3 -> {
                imageViewIndex = 1 // Volta para a primeira ImageView se atingir a terceira
                return findViewById(R.id.imagem3)
            }
            else -> return null
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}


class ExibicaoProdutoActivity : AppCompatActivity() {

    private val apiService = Conexao().createApiService()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private var productList: MutableList<Produto> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.exibicao_produto)

        // Botão Voltar
        val btnVoltar: ImageButton = findViewById(R.id.Voltar)
        btnVoltar.setOnClickListener {
            val exibeProdutoIntent = Intent(this, MainActivity::class.java)
            startActivity(exibeProdutoIntent)
        }

        // Categoria
        val spinnerTipo = findViewById<Spinner>(R.id.spinnerTipo)
        val adapterTipo = ArrayAdapter.createFromResource(
            this,
            R.array.tipos,
            R.layout.spinner_item_layout
        )
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapterTipo

        // Listar Produtos
        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager

        // Criar o adaptador uma vez
        adapter = ProductAdapter(productList)
        recyclerView.adapter = adapter

        // Fazer a chamada à API para obter a lista de produtos
        apiService.listarProdutosIds().enqueue(object : Callback<List<Int>> {
            override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {
                if (response.isSuccessful) {
                    // A resposta da API foi bem-sucedida
                    val productIds: List<Int>? = response.body()

                    if (!productIds.isNullOrEmpty()) {
                        // Agora, para cada ID, faça uma chamada para obter os detalhes do produto
                        for (productId in productIds) {
                            obterDetalhesProduto(productId)
                        }
                    } else {
                        // Lide com isso conforme necessário
                        exibirToast("Lista de produtos vazia")
                    }
                } else {
                    // A resposta da API não foi bem-sucedida
                    exibirToast("Erro na resposta da API: ${response.code()}")
                    try {
                        // Exibir detalhes do erro, se disponíveis
                        exibirToast("Detalhes do erro: ${response.errorBody()?.string()}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<List<Int>>, t: Throwable) {
                // Ocorreu uma falha na comunicação com a API
                // Lide com isso conforme necessário
                exibirToast("Falha na comunicação com a API. Mensagem: ${t.message}")
            }
        })
    }

    private fun obterDetalhesProduto(productId: Int) {
        apiService.obterProdutoPorId(productId).enqueue(object : Callback<Produto> {
            override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                if (response.isSuccessful) {
                    val produto: Produto? = response.body()
                    if (produto != null) {
                        exibirToast("Produto obtido: ${produto.title}")
                        exibirProdutoNoLayout(produto)
                    } else {
                        exibirToast("Produto não encontrado")
                        // Lide com isso conforme necessário
                    }
                } else {
                    exibirToast("Erro na resposta da API: ${response.code()}")
                    try {
                        // Exibir detalhes do erro, se disponíveis
                        exibirToast("Detalhes do erro: ${response.errorBody()?.string()}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<Produto>, t: Throwable) {
                exibirToast("Falha na comunicação com a API. Mensagem: ${t.message}")
                // Lide com isso conforme necessário
            }
        })
    }

    private fun exibirProdutoNoLayout(produto: Produto) {
        // Adicione o produto à lista do adaptador
        productList.add(produto)

        // Notifique o adaptador sobre a atualização na lista de produtos
        adapter.notifyDataSetChanged()
    }

    private fun exibirToast(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
    }

    // Acessar Produto
    fun abrirInterfaceEdicaoProduto() {
        val editeProdutoIntent = Intent(this, EdicaoProdutoActivity::class.java)
        startActivity(editeProdutoIntent)
    }
}





class MainActivity : AppCompatActivity() {

    private val apiService = Conexao().createApiService()

    private lateinit var txtTitulo: EditText
    private lateinit var txtDescricao: EditText
    private lateinit var txtPreco: EditText
    private lateinit var txtEstoque: EditText

    private var imageViewIndex = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cadastro_produto)

//  <<<<<<<<<<<<<<<<<<   Botão Voltar  >>>>>>>>>>>>>>>>>>
        val btnVoltar: ImageButton = findViewById(R.id.Voltar)

        btnVoltar.setOnClickListener {
            val exibeProdutoIntent = Intent(this, EdicaoProdutoActivity::class.java)
            startActivity(exibeProdutoIntent)
        }

//  <<<<<<<<<<<<<<<<<<   Categoria  >>>>>>>>>>>>>>>>>>
        // Recupera o `Spinner` Tipo
        val spinnerTipo = findViewById<Spinner>(R.id.spinnerTipo)
        val adapterTipo = ArrayAdapter.createFromResource(
            this,
            R.array.tipos,
            R.layout.spinner_item_layout
        )
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapterTipo

//  <<<<<<<<<<<<<<<<<<   Tempo de Produção  >>>>>>>>>>>>>>>>>>
        // Recupera o `Spinner` Prazo
        val spinnerPrazo = findViewById<Spinner>(R.id.spinnerPrazo)
        val adapterPrazo = ArrayAdapter.createFromResource(
            this,
            R.array.prazo,
            R.layout.spinner_item_layout
        )
        adapterPrazo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPrazo.adapter = adapterPrazo


//  <<<<<<<<<<<<<<<<<<   Botão Adicionar Foto  >>>>>>>>>>>>>>>>>>
        val adicionarFotoButton = findViewById<ImageButton>(R.id.adicionar_foto)
        adicionarFotoButton.setOnClickListener { openImagePicker() }

//  <<<<<<<<<<<<<<<<<<   Botão Confirmar  >>>>>>>>>>>>>>>>>
        val btnConfirmar: ImageButton = findViewById(R.id.btn_confirmar)

        this.txtTitulo = findViewById(R.id.txt_titulo1)
        this.txtDescricao = findViewById(R.id.txt_descricao)
        this.txtPreco = findViewById(R.id.txt_preco)
        this.txtEstoque = findViewById(R.id.txt_estoque)

        btnConfirmar.setOnClickListener {
            val produto = Produto(
                title = txtTitulo.text.toString(),
                category = spinnerTipo.selectedItem.toString(),
                description = txtDescricao.text.toString(),
                production_time = spinnerPrazo.selectedItem.toString(),
                price = txtPreco.text.toString(),
                stock = txtEstoque.text.toString()
            ).also {
                enviarDadosParaApi(it)
            }

        }
    }
    fun abrirInterfaceExibicaoProduto() {
        val exibeProdutoIntent = Intent(this, ExibicaoProdutoActivity::class.java)
        startActivity(exibeProdutoIntent)
    }
//  <<<<<<<<<<<<<<<<<<   Funções para Enviar os Dados  >>>>>>>>>>>>>>>>>>
    private fun enviarDadosParaApi(produto: Produto) {
        val ApiService = Conexao().createApiService()
        Log.i("api", ApiService.toString())

        val call = ApiService.enviarDados(produto)
        Log.i("api", call.toString())


        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (response.isSuccessful == true) {
                  Log.i("server-response",response.raw().toString())
                    Log.i("server-response",response.message())
                    // Dados enviados com sucesso
                    Toast.makeText(applicationContext, "Produto cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                    abrirInterfaceExibicaoProduto()
                } else {

                    // Tratar erro de resposta não bem-sucedida
                    Toast.makeText(applicationContext, "Erro ao enviar dados", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Tratar falha na requisição
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }



//  <<<<<<<<<<<<<<<<<<   Funções para Adicionar Foto  >>>>>>>>>>>>>>>>>>
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            if (data != null) {
                val selectedImageUri = data.data
                if (selectedImageUri != null) {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            selectedImageUri
                        )
                        val imageView: ImageView? = getNextImageView()
                        imageView?.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

//  <<<<<<<<<<<<<<<<<<   Função para Adicionar Foto na sequência >>>>>>>>>>>>>>>>>>
    @SuppressLint("WrongViewCast")
    private fun getNextImageView(): ImageView? {

    return when (imageViewIndex) {
        1 -> {
            imageViewIndex++
            findViewById(R.id.imagem1)
        }

        2 -> {
            imageViewIndex++
            findViewById(R.id.imagem2)
        }

        3 -> {
            imageViewIndex = 1 // Volta para a primeira ImageView se atingir a terceira
            findViewById(R.id.imagem3)
        }

        else -> null
    }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}







