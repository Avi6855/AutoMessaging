package com.avinashpatil.app.automessage.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.avinashpatil.app.automessage.data.entity.ContactEntity

object ContactHelper {
    
    fun getDeviceContacts(context: Context): List<ContactEntity> {
        val contacts = mutableListOf<ContactEntity>()
        val contentResolver: ContentResolver = context.contentResolver
        
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
        )
        
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )
        
        cursor?.use { contactCursor ->
            while (contactCursor.moveToNext()) {
                val contactId = contactCursor.getString(contactCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val displayName = contactCursor.getString(contactCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                val photoUri = contactCursor.getString(contactCursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))
                val hasPhoneNumber = contactCursor.getInt(contactCursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                
                if (hasPhoneNumber > 0) {
                    val phoneNumbers = getPhoneNumbers(contentResolver, contactId)
                    phoneNumbers.forEach { phoneNumber ->
                        val contact = ContactEntity(
                            id = contactId,
                            name = displayName,
                            phoneNumber = phoneNumber,
                            photoUri = photoUri,
                            groupId = null,
                            isPriority = false,
                            isBlacklisted = false,
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        contacts.add(contact)
                    }
                }
            }
        }
        
        return contacts
    }
    
    private fun getPhoneNumbers(contentResolver: ContentResolver, contactId: String): List<String> {
        val phoneNumbers = mutableListOf<String>()
        
        val phoneCursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(contactId),
            null
        )
        
        phoneCursor?.use { cursor ->
            while (cursor.moveToNext()) {
                val phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                phoneNumbers.add(phoneNumber)
            }
        }
        
        return phoneNumbers
    }
    
    fun getContactByPhoneNumber(context: Context, phoneNumber: String): ContactEntity? {
        val contentResolver: ContentResolver = context.contentResolver
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        
        val projection = arrayOf(
            ContactsContract.PhoneLookup._ID,
            ContactsContract.PhoneLookup.DISPLAY_NAME,
            ContactsContract.PhoneLookup.PHOTO_URI
        )
        
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        
        cursor?.use { contactCursor ->
            if (contactCursor.moveToFirst()) {
                val contactId = contactCursor.getString(contactCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
                val displayName = contactCursor.getString(contactCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
                val photoUri = contactCursor.getString(contactCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_URI))
                
                return ContactEntity(
                    id = contactId,
                    name = displayName,
                    phoneNumber = phoneNumber,
                    photoUri = photoUri,
                    groupId = null,
                    isPriority = false,
                    isBlacklisted = false,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            }
        }
        
        return null
    }
    
    fun formatPhoneNumber(phoneNumber: String): String {
        // Remove all non-digit characters
        val digitsOnly = phoneNumber.replace(Regex("[^0-9]"), "")
        
        // Handle different phone number formats
        return when {
            digitsOnly.length == 10 -> "+1$digitsOnly" // US number without country code
            digitsOnly.length == 11 && digitsOnly.startsWith("1") -> "+$digitsOnly" // US number with country code
            digitsOnly.length > 11 -> "+$digitsOnly" // International number
            else -> digitsOnly // Return as-is if format is unclear
        }
    }
}