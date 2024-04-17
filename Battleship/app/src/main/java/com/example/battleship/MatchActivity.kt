package com.example.battleship

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.battleship.databinding.ActivityMainBinding
import com.example.battleship.databinding.ActivityMatchBinding
import com.example.battleship.logic.Board
import com.example.battleship.logic.Game
import com.example.battleship.logic.Player
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MatchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatchBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database.reference

        binding.btnMatch.setOnClickListener{
            if (binding.txtMatchAlias.text.isEmpty()){
                Toast.makeText(this, R.string.match_activity_empty_alias, Toast.LENGTH_SHORT)
            } else {
                storageMatchInDB()
            }
        }
    }



    fun storageMatchInDB(){
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val loggedUserUid = sharedPref.getString(getString(R.string.user_logged_uid), "")
        val loggedUserEmail = sharedPref.getString(getString(R.string.user_logged_email), "")

        Log.d("log_user", loggedUserUid.toString())

        if (loggedUserUid.isNullOrEmpty() || loggedUserEmail.isNullOrEmpty()){
            // goToLogin()
            // return
        }

        print("Saving .. ")
        val rows = 10
        val cols = 10
        val board = Board(rows, cols)
        val player1 = Player("uid",  "uid")
        val player2 = null
        val alias = binding.txtMatchAlias.text.toString()

        val game = Game(board, alias, false, "", player1, player2)
        game.generateCells(rows, cols)

        val gameID = database.child("games").push().key;
        try {
            gameID?.let {
                database.child("games").child(it).setValue(game)
            }
        }catch (e: Exception){
            Toast.makeText(this, "Error registro", Toast.LENGTH_SHORT).show()
        }

        var intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    fun goToLogin(){
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}