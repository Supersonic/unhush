# Unhush

Proof-of-concept bypass for Android Do Not Disturb. A brief technical summary of the bypass can be found below, check out my [blog post](https://sithi.me/blog/2023-05-12-unhush/) for a high-level explanation!

This has been tested on a Pixel 7 Pro device running the latest Android update, but it should work on any Android device running R+ and has a properly implemented People and Conversations system.

## Summary

1. Create two `NotificationChannel` objects - the parent channel and the exploit channel.
2. Build a long-lived (`ShortcutInfo.Builder.setLongLived()`) dynamic shortcut and pushing it to the system using `ShortcutManager.pushDynamicShortcut()`.
3. Use `NotificationChannel.setConversationId()` to mark the exploit channel as a proper conversation-centric channel, by associating it with the parent channel and the shortcut.
4. Use reflection to mark the private field `mImportantConvo` in the exploit channel as accessible.
5. Set `mImportantConvo` to true.
6. Register both the parent channel an the exploit channel
7. The exploit channel would then be marked by the system as a priority conversation, and notifications posted to it would go through Do Not Disturb.

*NOTE: If you are an app developer, please don’t use this in your app! It’s not a security vulnerability, but it’s still unintended behavior made possible due to undocumented APIs. The Android feature team has been notified of this behavior and will likely fix it in the future. I’m not responsible if you implement this in your app and it gets banned from Play Store.*
