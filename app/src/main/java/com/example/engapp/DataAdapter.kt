package com.example.engapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.engapp.database.*

//Тут еще должны быть ебаные обработчики
//Передаем из фрагмента в адаптер Recycler
/* Второй аргумент говорит
0- Делаем список для раздела All works
1- Делаем список для раздела Favorite
2- Делаем список для раздела Profile
 */
class DataAdapter(private val idRecycler: Int, listWorks: List<ItemList?>?) :
    RecyclerView.Adapter<DataAdapter.ViewHolder>() {
    //Получаем экземпляры
    //Из этого листа вытягиваем id с помощью позиции
    private val listWoks = listWorks
    private var size =  listWorks!!.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {



        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_work, parent, false)

        return ViewHolder(view, idRecycler, listWoks)
    }

    override fun getItemCount(): Int {
        //Возвращаем количество элементов
        return size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(listWoks!![position]!!)

    }

    class ViewHolder internal constructor(view: View, private val idRecycler: Int,
                                          private val listWorks: List<ItemList?>? ) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        private var imageView: ImageView = view.findViewById<View>(R.id.imageWorks) as ImageView
        private var titleView: TextView = view.findViewById<View>(R.id.titleWorks) as TextView
        private var contentDescView: TextView = view.findViewById<View>(R.id.contentDescWorks) as TextView
        private var delAddButton: Button = view.findViewById(R.id.butDelAdd) as Button
        private val db: AppDatabase? = App.instance!!.database!!
        private val accountDao = db!!.accountDao()!!
        private val userDao = db!!.userDao()!!
        private val worksDao = db!!.worksDao()
        private val userId = userDao.getUserData()!!.userId

        init {
            delAddButton.setOnClickListener(this)
            view.setOnClickListener(this)
        }


        fun bind(itemWorks: ItemList) {
            if (itemWorks.pathImage == null) {
                imageView.setImageResource(R.drawable.photo_ots)
            } else { println("")//Тут вставим фото
                    }
            titleView.text = itemWorks.title
            contentDescView.text = itemWorks.contentDesc
        }

        override fun onClick(v: View?) {
            when(v!!.id){
                R.id.butDelAdd-> {
                    if(userId != null) {
                        val position: Int = adapterPosition
                        val workId = listWorks!![position]!!.id
                        val acRed = accountDao.getById(userId)
                        when (idRecycler) {
                            //Реализация добавления в раздел favorite
                            0 -> {
                                //Нашли данные о нашем аккаунте
                                //Вытянули id работы, по которой был сделан клик
                                val addFavorite = ListId(acRed!!.idFavorites)
                                addFavorite.addItem(workId)
                                acRed.idFavorites = addFavorite.getList().toString()
                                accountDao.update(acRed)
                            }
                            //Реализация удаления из Favorite
                            1 -> {
                                val delFavorite = ListId(acRed!!.idFavorites)
                                delFavorite.delItem(workId)
                                acRed.idFavorites = delFavorite.getList().toString()
                                accountDao.update(acRed)
                            }
                            //Реализация удаления из всей БД. Удаляем из списка его работ и всей бд
                            2 -> {
                                //Получаем наш аккаунт
                                val delWorks = ListId(acRed!!.idWorks)
                                val delFavor = ListId(acRed.idFavorites)
                                delFavor.delItem(workId)
                                delWorks.delItem(workId)
                                val del = worksDao!!.getById(workId)
                                acRed.idFavorites = delFavor.getList().toString()
                                acRed.idWorks = delWorks.getList().toString()
                                worksDao.deleteWorks(del)
                                accountDao.update(acRed)
                            }
                        }
                    }
                }
                R.layout.item_work-> {

                }

            }
        }
    }
}