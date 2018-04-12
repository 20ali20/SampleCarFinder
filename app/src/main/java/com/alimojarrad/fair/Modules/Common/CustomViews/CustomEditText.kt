package com.alimojarrad.fair.Modules.Common.CustomViews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.text.*
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.alimojarrad.fair.R
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.custom_edittext.view.*


/**
 * Created by amojarrad on 3/7/18.
 */
class CustomEditText : RelativeLayout {

    interface CustomEditTextListener {
        fun onRootClick() {}
        fun onEdittextClick() {}
        fun onRightDrawableClick() {}
    }

    enum class InputTypes(val value: Int) {
        PASSWORD_VISIBLE(0),
        PASSWORD_HIDDEN(1),
        TEXT(2),
        PHONE(3),
        NUMBER(4),
        EMAIL(5),
        ZIPCODE(6),
        DATE(7)
    }


    private var mInflater: LayoutInflater
    private lateinit var root: ConstraintLayout
    private lateinit var editText: EditText
    private lateinit var errorTextView: TextView
    private lateinit var floatingLabel: TextView
    private lateinit var hintTextView: TextView
    private lateinit var customView: View
    private lateinit var rightDrawable: ImageView

    var disableRightDrawableInteraction = false

    var hint = ""
    var maxLength: Int? = null
        set(value) {
            if (value != null) {
                editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(value))
            }
            field = value
        }

    var maxLines: Int? = null
        set(value) {
            if (value != null) {
                editText.maxLines = value!!
            }
            field = value
        }
    var error: String? = null
        get() = errorTextView.text.toString()
        set(value) {
            if (value.isNullOrEmpty()) {
                showSuccess()
            } else {
                showError(value!!)
            }
            field = value
        }

    var isSuccessful: Boolean = false
        set(value) {
            if (value) {
                showSuccess()
            }
            field = value
        }
    var text: String = ""
        get() {
            return when (inputType) {
                InputTypes.TEXT -> {
                    editText.text.toString()
                }
                InputTypes.NUMBER -> {
                    editText.text.toString()
                }
                InputTypes.PASSWORD_HIDDEN -> {
                    editText.text.toString()
                }
                InputTypes.PASSWORD_VISIBLE -> {
                    editText.text.toString()
                }
                InputTypes.PHONE -> {
                    editText.text.toString().replace("-", "")
                }
                InputTypes.EMAIL -> {
                    editText.text.toString()
                }
                InputTypes.ZIPCODE -> {
                    editText.text.toString()
                }
                InputTypes.DATE -> {
                    editText.text.toString()
                }

                null -> ""
            }
        }
        set(value) {
            editText.setText(value)
            field = value
        }

    var inputType: InputTypes? = InputTypes.TEXT
        set(value) {
            setupInputType(value!!)
            field = value
        }

    var hintOnfocus: String? = null
    var errorColor = 0
    var borderColor = 0
    var hintColor = 0
    var textColor = 0
    var floatingLabelColor = 0 //TODO: setup setter and getter for these


    var listener: CustomEditTextListener? = null


    private var disposable = CompositeDisposable()


    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        mInflater = LayoutInflater.from(context)
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mInflater = LayoutInflater.from(context)
        init(attrs)
    }


    private fun init(attrs: AttributeSet) {
        setupViews(attrs)
        setupInteractions()
    }

    private fun setupViews(attrs: AttributeSet) {
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomEditText, 0, 0)
        customView = mInflater.inflate(R.layout.custom_edittext, this, true)
        floatingLabel = customView.findViewById(R.id.customedittext_floating_label)
        editText = customView.findViewById(R.id.customedittext_edittext)
        hintTextView = customView.findViewById(R.id.customedittext_hint)
        errorTextView = customView.findViewById(R.id.customedittext_edittext_error)
        rightDrawable = customView.findViewById(R.id.customedittext_rightdrawable)
        root = customView.findViewById(R.id.customview_root)

        hint = ta.getString(R.styleable.CustomEditText_hint) ?: ""
        errorColor = ta.getColor(R.styleable.CustomEditText_errorColor, (Color.parseColor("#FB646B")))
        borderColor = ta.getColor(R.styleable.CustomEditText_borderColor, (Color.parseColor("#d45113")))
        hintColor = ta.getColor(R.styleable.CustomEditText_hintColor, (Color.parseColor("#AAAAAA")))
        textColor = ta.getColor(R.styleable.CustomEditText_textColor, (Color.parseColor("#434343")))
        floatingLabelColor = ta.getColor(R.styleable.CustomEditText_floatingLabelColor, (Color.parseColor("#64c5af")))
        hintOnfocus = ta.getString(R.styleable.CustomEditText_hintOnFocus)


        maxLength = ta.getInteger(R.styleable.CustomEditText_maxLength, 32)
        maxLines = ta.getInteger(R.styleable.CustomEditText_maxLines, 1)

        text = ta.getString(R.styleable.CustomEditText_text) ?: ""


        val textSize = ta.getDimension(R.styleable.CustomEditText_textSize, 18f)
        editText.textSize = textSize
        hintTextView.textSize = textSize
        floatingLabel.textSize = textSize

        val lineSpacingMultiplier = ta.getDimension(R.styleable.CustomEditText_lineSpacingMultiplier, 1f)
        editText.setLineSpacing(0f, lineSpacingMultiplier)

        errorTextView.setTextColor(errorColor)
        hintTextView.text = hint
        hintTextView.setTextColor(hintColor)
        editText.setTextColor(textColor)
        floatingLabel.setTextColor(floatingLabelColor)
        //customedittext_border.backgroundTintList = ColorStateList.valueOf(borderColor)
        floatingLabel.text = hintTextView.text

        //This is placed at the end to override predetermined values if the inputType requires it to do so
        inputType = InputTypes.values()[ta.getInt(R.styleable.CustomEditText_inputType, InputTypes.TEXT.ordinal)]

    }

    private fun setupInteractions() {
        root.setOnClickListener {
            listener?.let {
                it.onRootClick()
            }
        }
        editText.setOnClickListener {
            root.performClick()
            listener?.let {
                it.onEdittextClick()
            }
        }

        if (!disableRightDrawableInteraction) {
            rightDrawable.setOnClickListener {
                root.performClick()
                listener?.let {
                    it.onRightDrawableClick()
                }
            }
        }

        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hint.isNotEmpty()) {
                if (hasFocus) {
                    listener?.let {
                        it.onEdittextClick()
                    }
                    if (editText.text.isEmpty()) {
//                        hintTextView.animate().translationY(-10f).setDuration(100).start()
                        hintTextView.visibility = View.INVISIBLE
                        floatingLabel.visibility = View.VISIBLE
                        hintOnfocus?.let {
                            editText.setHintTextColor(ColorStateList.valueOf(hintColor))
                            editText.hint = it
                        }
                    }
                    customedittext_border.backgroundTintList = ColorStateList.valueOf(borderColor)

                } else if (!hasFocus) {
                    if (editText.text.isEmpty()) {
                        hintTextView.visibility = View.VISIBLE
                        editText.hint = ""
//                        hintTextView.animate().translationY(10f).setDuration(100).start()
                        floatingLabel.visibility = View.INVISIBLE
                    }
                    customedittext_border.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#AAAAAA"))
                }

            }
        }
    }


    private fun setupInputType(inputTypes: InputTypes) {
        when (inputTypes) {
            InputTypes.TEXT -> {
                editText.inputType = InputType.TYPE_CLASS_TEXT
            }

            InputTypes.NUMBER -> {
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            InputTypes.PASSWORD_HIDDEN -> {
                setupPasswordHidden()
            }
            InputTypes.PASSWORD_VISIBLE -> {
                setupPasswordVisible()
            }
            InputTypes.PHONE -> {
                setupPhone()
            }
            InputTypes.EMAIL -> {
                editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            InputTypes.ZIPCODE -> {
                setupZipCode()
            }
            InputTypes.DATE -> {
                setupDate()
            }

        }
    }

    private fun showError(value: String) {
        customedittext_border.backgroundTintList = ColorStateList.valueOf(errorColor)
        errorTextView.text = value
        floatingLabel.setTextColor(errorColor)
        rightDrawable.setImageResource(R.drawable.v_failure)
    }

    private fun showSuccess() {
        removeError()
        rightDrawable.setImageResource(R.drawable.v_success)
    }

    private fun removeError() {
        customedittext_border.backgroundTintList = ColorStateList.valueOf(borderColor)
        errorTextView.text = ""
        floatingLabel.setTextColor(floatingLabelColor)
    }

    private fun setupPhone() {
        editText.inputType = InputType.TYPE_CLASS_PHONE
        maxLength = 12
        editText.addTextChangedListener(
                object : TextWatcher {
                    private val dash = '-'
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable) {
                        if (s.isNotEmpty()) {
                            if (s.length == 4 || s.length == 8) {
                                val c = s[s.length - 1]
                                //Remove Dashes
                                if (dash === c) {
                                    s.delete(s.length - 1, s.length)
                                } else // Only if its a digit where there should be a space we insert a space
                                    if (Character.isDigit(c) && TextUtils.split(s.toString(), dash.toString()).size <= 2) {
                                        s.insert(s.length - 1, dash.toString())
                                    }
                            }
                        }
                    }
                })
    }

    private fun setupPasswordHidden() {
        disableRightDrawableInteraction = true
        editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        editText.transformationMethod = PasswordTransformationMethod.getInstance()

        showDrawable(rightDrawable, R.drawable.v_password_invisible)
        rightDrawable.setOnClickListener {
            when (editText.inputType) {
                InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                    editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    editText.transformationMethod = null
                    showDrawable(rightDrawable, R.drawable.v_password_visible)
                }
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD -> {
                    editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                    editText.transformationMethod = PasswordTransformationMethod.getInstance()
                    showDrawable(rightDrawable, R.drawable.v_password_invisible)
                }
            }
        }
    }

    private fun setupPasswordVisible() {
        editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        rightDrawable.setImageResource(R.drawable.v_password_visible)
        showDrawable(rightDrawable, R.drawable.v_password_visible)
    }

    private fun setupZipCode() {
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        maxLength = 5
    }

    private fun setupDate() {
        editText.isCursorVisible = false
        editText.inputType = InputType.TYPE_CLASS_DATETIME
        maxLength = 10
        editText.addTextChangedListener(
                object : TextWatcher {
                    private val dash = '-'
                    var currentPosition = 0
                    var prevPosition = 0
                    var isRemoving = false
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        currentPosition = s.length
                        isRemoving = currentPosition<prevPosition
                    }
                    override fun afterTextChanged(s: Editable) {
                        if ((s.length == 4 || s.length == 7) && !isRemoving) {
                            s.append(dash)
                        }
                        if ((s.length == 5 || s.length == 8) && isRemoving){
                            s.substring(0,s.length-1)
                        }
                        prevPosition = currentPosition


                    }
                })
    }

    private fun showDrawable(imageView: ImageView, drawable: Int) {
        imageView.setImageDrawable(resources.getDrawable(drawable, null))
    }

    private fun hideDrawable(imageView: ImageView) {
        imageView.setImageDrawable(null)
    }


}