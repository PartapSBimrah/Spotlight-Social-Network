/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.spots.SpotMaker.SpotMakerView.Objects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.LineBackgroundSpan;
import android.util.AttributeSet;

import org.json.JSONObject;

import androidx.appcompat.widget.AppCompatEditText;
import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontFamlie;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextAlignment;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextShader;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontStyle;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;

public class SpotMakerEdittext extends AppCompatEditText implements LineBackgroundSpan, TextWatcher
{
    private EsaphShader esaphShader;

    public SpotMakerEdittext(Context context)
    {
        super(context);
        setPadding(SpotMakerEdittext.padding, SpotMakerEdittext.padding, SpotMakerEdittext.padding, SpotMakerEdittext.padding);
        setShadowLayer(padding, 0f, 0f, 0);
        addTextChangedListener(this);
    }

    public SpotMakerEdittext(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setPadding(SpotMakerEdittext.padding, SpotMakerEdittext.padding, SpotMakerEdittext.padding, SpotMakerEdittext.padding);
        setShadowLayer(padding, 0f, 0f, 0);
        addTextChangedListener(this);
    }

    public SpotMakerEdittext(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setPadding(SpotMakerEdittext.padding, SpotMakerEdittext.padding, SpotMakerEdittext.padding, SpotMakerEdittext.padding);
        setShadowLayer(padding, 0f, 0f, 0);
        addTextChangedListener(this);
    }

    public void onValuesChanged(JSONObject jsonObject)
    {
        try
        {
            setTextColor(SpotTextDefinitionBuilder.getTextColor(jsonObject));
            setHintTextColor(SpotTextDefinitionBuilder.getTextColor(jsonObject));
            setTextSize(SpotTextDefinitionBuilder.getTextSize(jsonObject));
            setGravity(SpotTextAlignment.getGravityForApiVersion(SpotTextDefinitionBuilder.getTextAlignment(jsonObject)));

            int backgroundColor = SpotBackgroundDefinitionBuilder.getBackgroundColor(jsonObject);
            paintBackground.setColor(backgroundColor);
            paintStroke.setColor(backgroundColor);

            Typeface typefaceTextStyle = SpotTextFontStyle.getFontStyleForApiVersion(SpotTextDefinitionBuilder.getFontStyle(jsonObject));
            Typeface typefaceFontFamilie = SpotTextFontFamlie.getFontFamlieForApiVersion(getContext(), SpotTextDefinitionBuilder.getFontFamily(jsonObject));

            setTypeface(Typeface.create(typefaceFontFamilie, typefaceTextStyle.getStyle()));

            if(SpotTextDefinitionBuilder.hasShader(jsonObject))
            {
                setEsaphShader(SpotTextShader.getShaderForApiVersion(SpotTextDefinitionBuilder.getTextShader(jsonObject)));
                setLayerType(LAYER_TYPE_SOFTWARE, getPaint());
            }
            else
            {
                setEsaphShader(null);
            }
        }
        catch (Exception ec)
        {
        }
        finally
        {
            requestLayout();
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        if(this.esaphShader != null)
        {
            this.esaphShader.onLayout(getHeight(), getWidth());
            this.esaphShader.onDrawShader(getPaint());
        }
        else
        {
            getPaint().setShader(null);
            getPaint().clearShadowLayer();
        }
    }

    private void setEsaphShader(EsaphShader esaphShader)
    {
        this.esaphShader = esaphShader;
    }


    private static int padding = DisplayUtils.dp2px(8);
    private static int radius = DisplayUtils.dp2px(6);

    private RectF rect = new RectF();
    private Paint paintBackground = new Paint();
    private Paint paintStroke = new Paint();
    private Path path = new Path();

    private float prevWidth = -1f;
    private float prevLeft = -1f;
    private float prevRight = -1f;
    private float prevBottom = -1f;
    private float prevTop = -1f;

    @Override
    public void drawBackground(
            final Canvas c,
            final Paint p,
            final int left,
            final int right,
            final int top,
            final int baseline,
            final int bottom,
            final CharSequence text,
            final int start,
            final int end,
            final int lnum)
    {
        if(this.esaphShader != null)
        {
            this.esaphShader.onLayout(getHeight(), getWidth());
            this.esaphShader.onDrawShader(p);
        }
        else
        {
            p.setShader(null);
            p.clearShadowLayer();
            p.setShadowLayer(padding, 0f, 0f, 0);
        }

        float width = p.measureText(text, start, end) + 2f * SpotMakerEdittext.padding;
        float shift = (right - width) / 2f;

        rect.set(shift, top, right - shift, bottom);

        if (lnum == 0) {
            c.drawRoundRect(rect, SpotMakerEdittext.radius, SpotMakerEdittext.radius, paintBackground);
        } else {
            path.reset();
            float dr = width - prevWidth;
            float diff = -Math.signum(dr) * Math.min(2f * SpotMakerEdittext.radius, Math.abs(dr/2f))/2f;
            path.moveTo(
                    prevLeft, prevBottom - SpotMakerEdittext.radius
            );

            path.cubicTo(
                    prevLeft, prevBottom - SpotMakerEdittext.radius,
                    prevLeft, rect.top,
                    prevLeft + diff, rect.top
            );
            path.lineTo(
                    rect.left - diff, rect.top
            );
            path.cubicTo(
                    rect.left - diff, rect.top,
                    rect.left, rect.top,
                    rect.left, rect.top + SpotMakerEdittext.radius
            );
            path.lineTo(
                    rect.left, rect.bottom - SpotMakerEdittext.radius
            );
            path.cubicTo(
                    rect.left, rect.bottom - SpotMakerEdittext.radius,
                    rect.left, rect.bottom,
                    rect.left + SpotMakerEdittext.radius, rect.bottom
            );
            path.lineTo(
                    rect.right - SpotMakerEdittext.radius, rect.bottom
            );
            path.cubicTo(
                    rect.right - SpotMakerEdittext.radius, rect.bottom,
                    rect.right, rect.bottom,
                    rect.right, rect.bottom - SpotMakerEdittext.radius
            );
            path.lineTo(
                    rect.right, rect.top + SpotMakerEdittext.radius
            );
            path.cubicTo(
                    rect.right, rect.top + SpotMakerEdittext.radius,
                    rect.right, rect.top,
                    rect.right + diff, rect.top
            );
            path.lineTo(
                    prevRight - diff, rect.top
            );
            path.cubicTo(
                    prevRight - diff, rect.top,
                    prevRight, rect.top,
                    prevRight, prevBottom - SpotMakerEdittext.radius
            );
            path.cubicTo(
                    prevRight, prevBottom - SpotMakerEdittext.radius,
                    prevRight, prevBottom,
                    prevRight - SpotMakerEdittext.radius, prevBottom

            );
            path.lineTo(
                    prevLeft + SpotMakerEdittext.radius, prevBottom
            );
            path.cubicTo(
                    prevLeft + SpotMakerEdittext.radius, prevBottom,
                    prevLeft, prevBottom,
                    prevLeft, rect.top - SpotMakerEdittext.radius
            );
            c.drawPath(path, paintStroke);
        }

        prevWidth = width;
        prevLeft = rect.left;
        prevRight = rect.right;
        prevBottom = rect.bottom;
        prevTop = rect.top;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s)
    {
        s.setSpan(this, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
