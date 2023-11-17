package com.example.quilon_interface

import Produto
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EdicaoProdutoActivity : AppCompatActivity() {

    private val apiService = ApiClient().createApiService()

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
        val produtoId = intent.getIntExtra("produtoId", -1)

        // Verifica se o ID é válido
        if (produtoId == -1) {
            Toast.makeText(applicationContext, "ID do produto inválido", Toast.LENGTH_SHORT).show()
            // Lida com a situação de ID inválido conforme necessário
            return
        }
        val call = apiService.receberProduto(produtoId)

        call.enqueue(object : Callback<Produto> {
            override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                if (response.isSuccessful) {
                    val produto = response.body()

                    // Log para verificar se os dados do produto estão corretos
                    //Toast.makeText(applicationContext, "Produto recebido: $produto", Toast.LENGTH_SHORT).show()

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

                    //  <<<<<<<<<<<<<<<<<<   Preço  >>>>>>>>>>>>>>>>>>
                    val txtPreco = findViewById<EditText>(R.id.txt_preco)
                    txtPreco.setText(produto?.price.toString())

                    txtPreco.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
                        if (source.toString().matches("[0-9.]*".toRegex())) {
                            source
                        } else {
                            ""
                        }
                    })

                    //  <<<<<<<<<<<<<<<<<<   Estoque  >>>>>>>>>>>>>>>>>>
                    val txtEstoque = findViewById<EditText>(R.id.txt_estoque)
                    txtEstoque.setText(produto?.stock.toString())

                    txtEstoque.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
                        if (source.toString().matches("[0-9]*".toRegex())) {
                            source
                        } else {
                            ""
                        }
                    })

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
        imageView1.setImageResource(R.drawable.imagem_do_produto)

//  <<<<<<<<<<<<<<<<<<   Imagem 2  >>>>>>>>>>>>>>>>>>
        val imageView2: ImageView = findViewById(R.id.imagem2)
        imageView2.setImageResource(R.drawable.imagem_do_produto)

//  <<<<<<<<<<<<<<<<<<   Imagem 3  >>>>>>>>>>>>>>>>>>
        val imageView3: ImageView = findViewById(R.id.imagem3)
        imageView3.setImageResource(R.drawable.imagem_do_produto)

//  <<<<<<<<<<<<<<<<<<   Botão Salvar  >>>>>>>>>>>>>>>>>>
        val btnSalvar: ImageButton = findViewById(R.id.btn_salvar)

        btnSalvar.setOnClickListener {
            // Verifica se todos os campos estão preenchidos
            if (camposPreenchidos()) {
                // Obtém os dados atualizados da tela
                val novoTitulo = findViewById<EditText>(R.id.txt_titulo1).text.toString()
                val novaCategoria = findViewById<Spinner>(R.id.spinnerTipo).selectedItem.toString()
                val novaDescricao = findViewById<EditText>(R.id.txt_descricao).text.toString()
                val novoPrazo = findViewById<Spinner>(R.id.spinnerPrazo).selectedItem.toString()
                val novoPreco = findViewById<EditText>(R.id.txt_preco).text.toString()
                val novoEstoque = findViewById<EditText>(R.id.txt_estoque).text.toString()

                // Verifica se os valores dos spinners são válidos
                if (novaCategoria != "Selecione o tipo" && novoPrazo != "Selecione o prazo") {
                    // Monta um objeto Produto com os dados atualizados
                    val produtoAtualizado = Produto(
                        id = produtoId,
                        title = novoTitulo,
                        category = novaCategoria,
                        description = novaDescricao,
                        production_time = novoPrazo,
                        price = novoPreco,
                        stock = novoEstoque
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
                } else {
                    Toast.makeText(applicationContext, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
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

    private fun camposPreenchidos(): Boolean {
        val txtTitulo = findViewById<EditText>(R.id.txt_titulo1)
        val spinnerTipo = findViewById<Spinner>(R.id.spinnerTipo)
        val txtDescricao = findViewById<EditText>(R.id.txt_descricao)
        val spinnerPrazo = findViewById<Spinner>(R.id.spinnerPrazo)
        val txtPreco = findViewById<EditText>(R.id.txt_preco)
        val txtEstoque = findViewById<EditText>(R.id.txt_estoque)

        return !txtTitulo.text.isNullOrBlank() &&
                spinnerTipo.selectedItemPosition != 0 &&
                !txtDescricao.text.isNullOrBlank() &&
                spinnerPrazo.selectedItemPosition != 0 &&
                !txtPreco.text.isNullOrBlank() &&
                !txtEstoque.text.isNullOrBlank()
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

    private val apiService = ApiClient().createApiService()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private var productList: MutableList<Produto> = mutableListOf()
    private var selectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.exibicao_produto)

        // Botão Voltar
        val btnVoltar: ImageButton = findViewById(R.id.Voltar)
        btnVoltar.setOnClickListener {
            val exibeProdutoIntent = Intent(this, MainActivity::class.java)
            startActivity(exibeProdutoIntent)
        }

        // Barra de Busca
        val editTextSearch = findViewById<EditText>(R.id.editTextSearch)
        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchTerm = editTextSearch.text.toString()
                buscarProdutosPorTermo(searchTerm)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
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
        adapter = ProductAdapter(productList) { productId ->
            abrirInterfaceEdicaoProduto(productId)
        }
        recyclerView.adapter = adapter

        // Fazer a chamada à API para obter a lista de produtos
        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Verifique se "Selecione o tipo" foi selecionado
                if (position == 0) {
                    // Chame a API sem passar a categoria
                    listarProdutosIdsPorCategoria(null)
                } else {
                    // Atualize a categoria selecionada e chame a API com a categoria
                    selectedCategory = parent?.getItemAtPosition(position).toString()
                    listarProdutosIdsPorCategoria(selectedCategory)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Não é necessário implementar nada aqui
            }
        }

    }

    private fun buscarProdutosPorTermo(searchTerm: String) {
        apiService.buscarProdutosPorTermo(searchTerm).enqueue(object : Callback<List<Int>> {
            override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {
                if (response.isSuccessful) {
                    val productIds: List<Int>? = response.body()

                    if (!productIds.isNullOrEmpty()) {
                        productList.clear()

                        for (productId in productIds) {
                            obterDetalhesProduto(productId)
                        }
                    } else {
                        exibirToast("Nenhum produto encontrado para o termo de busca: $searchTerm")
                    }
                } else {
                    exibirToast("Erro na resposta da API: ${response.code()}")
                    try {
                        exibirToast("Detalhes do erro: ${response.errorBody()?.string()}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<List<Int>>, t: Throwable) {
                exibirToast("Falha na comunicação com a API. Mensagem: ${t.message}")
            }
        })
    }

    private fun listarProdutosIdsPorCategoria(category: String?) {
        apiService.listarProdutosIdsPorCategoria(category).enqueue(object : Callback<List<Int>> {
            override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {
                if (response.isSuccessful) {
                    val productIds: List<Int>? = response.body()

                    if (!productIds.isNullOrEmpty()) {
                        // Limpar a lista existente antes de adicionar os novos produtos
                        productList.clear()

                        for (productId in productIds) {
                            obterDetalhesProduto(productId)
                        }
                    } else {
                        exibirToast("Nenhum produto encontrado para a categoria: $category")
                    }
                } else {
                    exibirToast("Erro na resposta da API: ${response.code()}")
                    try {
                        exibirToast("Detalhes do erro: ${response.errorBody()?.string()}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<List<Int>>, t: Throwable) {
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
    fun abrirInterfaceEdicaoProduto(produtoId: Int) {
        val editeProdutoIntent = Intent(this, EdicaoProdutoActivity::class.java)
        editeProdutoIntent.putExtra("produtoId", produtoId)
        startActivity(editeProdutoIntent)
    }
}


class MainActivity : AppCompatActivity() {

    private val apiService = ApiClient().createApiService()

    private lateinit var txtTitulo: EditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var txtDescricao: EditText
    private lateinit var spinnerPrazo: Spinner
    private lateinit var txtPreco: EditText
    private lateinit var txtEstoque: EditText

    private var imageViewIndex = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cadastro_produto)

//  <<<<<<<<<<<<<<<<<<   Botão Voltar  >>>>>>>>>>>>>>>>>>
        val btnVoltar: ImageButton = findViewById(R.id.Voltar)

        btnVoltar.setOnClickListener {
            val exibeProdutoIntent = Intent(this, ExibicaoProdutoActivity::class.java)
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

//  <<<<<<<<<<<<<<<<<<   Preço  >>>>>>>>>>>>>>>>>>
        // Recupera o `EditText` para o campo de preço
        val txtPreco = findViewById<EditText>(R.id.txt_preco)

        // Adiciona InputFilter para aceitar apenas números e ponto decimal
        txtPreco.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            if (source.toString().matches("[0-9.]*".toRegex())) {
                source
            } else {
                ""
            }
        })

//  <<<<<<<<<<<<<<<<<<   Estoque  >>>>>>>>>>>>>>>>>>
        val txtEstoque = findViewById<EditText>(R.id.txt_estoque)

        txtEstoque.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            if (source.toString().matches("[0-9]*".toRegex())) {
                source
            } else {
                ""
            }
        })

//  <<<<<<<<<<<<<<<<<<   Botão Adicionar Foto  >>>>>>>>>>>>>>>>>>
        val adicionarFotoButton = findViewById<ImageButton>(R.id.adicionar_foto)
        adicionarFotoButton.setOnClickListener { openImagePicker() }

//  <<<<<<<<<<<<<<<<<<   Botão Confirmar  >>>>>>>>>>>>>>>>>
        val btnConfirmar: ImageButton = findViewById(R.id.btn_confirmar)

        this.txtTitulo = findViewById(R.id.txt_titulo1)
        this.txtDescricao = findViewById(R.id.txt_descricao)
        this.txtPreco = findViewById(R.id.txt_preco)
        this.txtEstoque = findViewById(R.id.txt_estoque)
        this.spinnerTipo = findViewById(R.id.spinnerTipo)
        this.spinnerPrazo = findViewById(R.id.spinnerPrazo)

        btnConfirmar.setOnClickListener {
            if (camposPreenchidos()) {
                val produto = Produto(
                    id = null,
                    title = txtTitulo.text.toString(),
                    category = spinnerTipo.selectedItem.toString(),
                    description = txtDescricao.text.toString(),
                    production_time = spinnerPrazo.selectedItem.toString(),
                    price = txtPreco.text.toString(),
                    stock = txtEstoque.text.toString()
                ).also {
                    enviarDadosParaApi(it)
                }
            } else {
                Toast.makeText(applicationContext, "Preencha todos os campos antes de confirmar", Toast.LENGTH_SHORT).show()
            }
        }
    }

//  <<<<<<<<<<<<<<<<<<   Função para Verificar se todos os campos foram preenchidos  >>>>>>>>>>>>>>>>>>
    private fun camposPreenchidos(): Boolean {
        val tipoSelecionado = spinnerTipo.selectedItem.toString()
        val prazoSelecionado = spinnerPrazo.selectedItem.toString()

        return !txtTitulo.text.isNullOrBlank() &&
                !txtDescricao.text.isNullOrBlank() &&
                !txtPreco.text.isNullOrBlank() &&
                !txtEstoque.text.isNullOrBlank() &&
                tipoSelecionado != "Selecione o tipo" &&
                prazoSelecionado != "Selecione o prazo"
    }

//  <<<<<<<<<<<<<<<<<<   Funções para Enviar os Dados  >>>>>>>>>>>>>>>>>>
    private fun enviarDadosParaApi(produto: Produto) {
        val ApiService = ApiClient().createApiService()
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

//  <<<<<<<<<<<<<<<<<<   Função para abrir a Interface de Exibição dos Produtos  >>>>>>>>>>>>>>>>>>
    fun abrirInterfaceExibicaoProduto() {
        val exibeProdutoIntent = Intent(this, ExibicaoProdutoActivity::class.java)
        startActivity(exibeProdutoIntent)
    }
}

