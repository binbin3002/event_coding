package com.example.eventdicoding.ui.detail

import android.content.Intent
import android.content.res.Configuration
import androidx.core.content.ContextCompat
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import com.example.eventdicoding.R
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.eventdicoding.data.Result
import com.example.eventdicoding.data.local.entity.EventEntity
import com.example.eventdicoding.databinding.ActivityDetailBinding
import com.example.eventdicoding.ui.setting.SettingPreferences
import com.example.eventdicoding.ui.setting.dataStore
import com.example.eventdicoding.ui.viewmodel.MainViewModel
import com.example.eventdicoding.ui.viewmodel.ViewModelFactory
import com.example.eventdicoding.util.SimpleDateUtil.formatDateTime

class DetailActivity : AppCompatActivity() {
    private  var _binding : ActivityDetailBinding? = null
    private  val binding get() = _binding!!
    private  lateinit var pref : SettingPreferences
    private  val viewModel: MainViewModel by viewModels{
        ViewModelFactory.getInstance(this, pref)
    }
    private  lateinit var  progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)
        progressBar = binding.progressBar
        pref = SettingPreferences.getInstance(this.applicationContext.dataStore)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.detail_event)

        if (eventId  !=  null){
            observeEventDetail(eventId)
        }
    }
    private  fun observeEventDetail(eventId: String){
        viewModel.getDetailEvent(eventId).observe(this){result ->
            when (result){
                is Result.Loading->{
                    progressBar.visibility = View.VISIBLE
                }
                is Result.Success->{
                    progressBar.visibility = View.GONE
                    updateUI(result.data)
                }
                is Result.Error ->{
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private  fun updateUI(eventDetail: EventEntity){
        binding.apply {
            Glide.with(this@DetailActivity)
                .load(eventDetail.imageLogo)
                .into(ivDetailEventImageLogo)

            tvDetailEventName.text = eventDetail.name
            tvDetailEventOwner.text = eventDetail.ownerName
            "${formatDateTime(eventDetail.beginTime)} - ${formatDateTime(eventDetail.endTime)}".also { tvDetailEventTime.text = it }
            "Available Quota: ${eventDetail.quota.minus(eventDetail.registrants)}".also { tvDetailEventQuota.text = it }
            tvDetailEventDescription.text = HtmlCompat.fromHtml(eventDetail.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            val drawable  = ContextCompat.getDrawable(this@DetailActivity, R.drawable.icon_calender )
            val drawable1  = ContextCompat.getDrawable(this@DetailActivity, R.drawable.icon_apartment )
            val drawable2  = ContextCompat.getDrawable(this@DetailActivity, R.drawable.icon_group )

            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES){
                drawable?.setTint(ContextCompat.getColor(this@DetailActivity, R.color.white))
                drawable1?.setTint(ContextCompat.getColor(this@DetailActivity, R.color.white))
                drawable2?.setTint(ContextCompat.getColor(this@DetailActivity, R.color.white))

            }else{
                drawable?.setTint(ContextCompat.getColor(this@DetailActivity , R.color.black))
                drawable1?.setTint(ContextCompat.getColor(this@DetailActivity , R.color.black))
                drawable2?.setTint(ContextCompat.getColor(this@DetailActivity, R.color.black))
            }
            tvDetailEventTime.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            tvDetailEventOwner.setCompoundDrawablesWithIntrinsicBounds(drawable1, null, null, null)
            tvDetailEventQuota.setCompoundDrawablesWithIntrinsicBounds(drawable2, null, null, null)

            btnDetailEventOpenLink.setOnClickListener{
                eventDetail.link.let { link ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    startActivity(intent)
                }
                if(eventDetail.link == null){
                Toast.makeText(this@DetailActivity,
                    getString(R.string.no_link_found), Toast.LENGTH_SHORT).show()
            }
            }
            if (eventDetail.isFavorited){
               btnFavorite.background = AppCompatResources.getDrawable(this@DetailActivity, R.drawable.icon_favorite)
            }else{
                btnFavorite.background = AppCompatResources.getDrawable(this@DetailActivity, R.drawable.icon_unfavorite)
            }

            btnFavorite.setOnClickListener{
                eventDetail.isFavorited = !eventDetail.isFavorited
                binding.btnFavorite.background =
                    if (eventDetail.isFavorited){
                    AppCompatResources.getDrawable(this@DetailActivity, R.drawable.icon_favorite)
                }else{
                        AppCompatResources.getDrawable(this@DetailActivity, R.drawable.icon_unfavorite)
                    }
                viewModel.updateFavoriteStatus(eventDetail, eventDetail.isFavorited)



                val message = if (eventDetail.isFavorited) getString(R.string.added_to_favorites) else getString(
                    R.string.removed_from_favorites
                )
                Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }



    companion object{
        const val  EXTRA_EVENT_ID = "extra_event_id"
    }
}