package com.programmersbox.kotlinktslearn

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.programmersbox.dragswipe.DragSwipeAdapter
import com.programmersbox.dragswipe.shuffleItems
import com.programmersbox.flowutils.clicks
import com.programmersbox.flowutils.collectOnUi
import com.programmersbox.funutils.cards.Deck
import com.programmersbox.helpfulutils.layoutInflater
import com.programmersbox.loggingutils.Loged
import com.programmersbox.loggingutils.f
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.child_item.view.*
import kotlinx.android.synthetic.main.person_item.view.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val personDeck = Deck(Names.names.map {
        PersonBuilder.builder2 {
            name(it)
            age(Random.nextInt(1, 100))
            birthday { it + 1 }
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logedSetup()

        Loged.f("Refresh Rate: ${windowManager.defaultDisplay.refreshRate}")

        val adapter = PersonAdapter(Names.names.map {
            PersonBuilder.builder {
                name(it)
                age(Random.nextInt(1, 100))
                birthday { it + 1 }
            }
        }.toMutableList(), this)

        personRV.adapter = adapter

        createNotificationChannel("id_channel")
        createNotificationGroup("id_group")

        shufflePeople
            .clicks()
            .collectOnUi {
                sendReplyNotification(
                    R.mipmap.ic_launcher,
                    R.mipmap.ic_launcher,
                    "Title",
                    "Message",
                    "42",
                    "Title",
                    "Message",
                    "id_channel",
                    MainActivity::class.java,
                    42
                ) {
                    println(it)
                }

                sendNotification(423) {
                    smallIconId = R.mipmap.ic_launcher
                    title = "Hello"
                    message = "World"
                    channelId = "id_channel"

                    addAction {
                        actionTitle = "Action!"
                        actionIcon = R.mipmap.ic_launcher
                        this.pendingActivity(ActionService::class.java)
                    }

                    addReplyAction {
                        actionTitle = "Reply!"
                        actionIcon = R.mipmap.ic_launcher
                        resultKey = KEY_TEXT_REPLY
                        label = "Reply here"
                        pendingActivity(ReplyService::class.java)
                    }
                }
            }

    }

    private fun logedSetup() {
        Loged.TAG = "KtsLearn"
        Loged.FILTER_BY_PACKAGE_NAME = "programmersbox"
    }
}

class PersonAdapter(dataList: MutableList<Person>, private val context: Context) : EasyAdapter<Person, EasyViewHolder<Person>>(dataList) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EasyViewHolder<Person> = when (viewType) {
        R.layout.child_item -> ChildHolder(context.layoutInflater.inflate(viewType, parent, false))
        else -> PersonHolder(context.layoutInflater.inflate(viewType, parent, false))
    }


    override fun EasyViewHolder<Person>.onBind(item: Person, position: Int) = render(item)

    override fun itemViewType(item: Person, position: Int): Int = when (item) {
        is Person.Adult -> R.layout.person_item
        is Person.Child -> R.layout.child_item
    }
}

class PersonHolder(itemView: View) : EasyViewHolder<Person>(itemView) {

    private val name = itemView.personName!!
    private val age = itemView.personAge!!

    override fun render(item: Person) {
        name.text = item.name
        age.text = "${item.age}"
        itemView
            .clicks()
            .collectOnUi {
                item.birthdayDay()
                age.text = "${item.age}"
            }
    }
}

class ChildHolder(itemView: View) : EasyViewHolder<Person>(itemView) {

    private val name = itemView.childName!!
    private val age = itemView.childAge!!

    override fun render(item: Person) {
        name.text = item.name
        age.text = "${item.age}"
        itemView
            .clicks()
            .collectOnUi {
                item.birthdayDay()
                age.text = "${item.age}"
            }
    }
}

abstract class EasyAdapter<T, VH : EasyViewHolder<T>>(dataList: MutableList<T>) : DragSwipeAdapter<T, VH>(dataList) {
    override fun VH.onBind(item: T, position: Int) = render(item)
    override fun getItemViewType(position: Int): Int = itemViewType(dataList[position], position)
    open fun itemViewType(item: T, position: Int) = super.getItemViewType(position)
}

abstract class EasyViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun render(item: T)
}