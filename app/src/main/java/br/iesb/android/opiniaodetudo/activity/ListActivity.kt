package br.iesb.android.opiniaodetudo.activity


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.iesb.android.opiniaodetudo.R
import br.iesb.android.opiniaodetudo.fragment.ListFragment
import br.iesb.android.opiniaodetudo.model.Review

class ListActivity : AppCompatActivity() {

    private lateinit var reviews : MutableList<Review>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_review_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, ListFragment())
            .commit()
    }
}
