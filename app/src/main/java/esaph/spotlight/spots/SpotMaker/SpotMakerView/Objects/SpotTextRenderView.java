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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.LineBackgroundSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import org.json.JSONObject;

import esaph.spotlight.Esaph.EsaphCircleImageView.EsaphShader;
import esaph.spotlight.Esaph.EsaphTextViewsAnimated.DisplayUtils;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotBackgroundDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Definitions.SpotTextDefinitionBuilder;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextAlignment;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontFamlie;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextFontStyle;
import esaph.spotlight.spots.SpotMaker.Models.SpotTextShader;

public class SpotTextRenderView extends AppCompatTextView implements LineBackgroundSpan
{
    private EsaphShader esaphShader;

    public SpotTextRenderView(Context context)
    {
        super(context);
        setPadding(SpotTextRenderView.padding, SpotTextRenderView.padding, SpotTextRenderView.padding, SpotTextRenderView.padding);
        setShadowLayer(padding, 0f, 0f, 0);
    }

    public SpotTextRenderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setPadding(SpotTextRenderView.padding, SpotTextRenderView.padding, SpotTextRenderView.padding, SpotTextRenderView.padding);
        setShadowLayer(padding, 0f, 0f, 0);
    }

    public SpotTextRenderView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setPadding(SpotTextRenderView.padding, SpotTextRenderView.padding, SpotTextRenderView.padding, SpotTextRenderView.padding);
        setShadowLayer(padding, 0f, 0f, 0);
    }

    public void onValuesChanged(JSONObject jsonObject, String Text)
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
            SpannableString spannableString = new SpannableString(Text);
            spannableString.setSpan(this, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(spannableString);
            requestLayout();
            invalidate();
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

        float width = p.measureText(text, start, end) + 2f * SpotTextRenderView.padding;
        float shift = (right - width) / 2f;

        rect.set(shift, top, right - shift, bottom);

        if (lnum == 0) {
            c.drawRoundRect(rect, SpotTextRenderView.radius, SpotTextRenderView.radius, paintBackground);
        } else {
            path.reset();
            float dr = width - prevWidth;
            float diff = -Math.signum(dr) * Math.min(2f * SpotTextRenderView.radius, Math.abs(dr/2f))/2f;
            path.moveTo(
                    prevLeft, prevBottom - SpotTextRenderView.radius
            );

            path.cubicTo(
                    prevLeft, prevBottom - SpotTextRenderView.radius,
                    prevLeft, rect.top,
                    prevLeft + diff, rect.top
            );
            path.lineTo(
                    rect.left - diff, rect.top
            );
            path.cubicTo(
                    rect.left - diff, rect.top,
                    rect.left, rect.top,
                    rect.left, rect.top + SpotTextRenderView.radius
            );
            path.lineTo(
                    rect.left, rect.bottom - SpotTextRenderView.radius
            );
            path.cubicTo(
                    rect.left, rect.bottom - SpotTextRenderView.radius,
                    rect.left, rect.bottom,
                    rect.left + SpotTextRenderView.radius, rect.bottom
            );
            path.lineTo(
                    rect.right - SpotTextRenderView.radius, rect.bottom
            );
            path.cubicTo(
                    rect.right - SpotTextRenderView.radius, rect.bottom,
                    rect.right, rect.bottom,
                    rect.right, rect.bottom - SpotTextRenderView.radius
            );
            path.lineTo(
                    rect.right, rect.top + SpotTextRenderView.radius
            );
            path.cubicTo(
                    rect.right, rect.top + SpotTextRenderView.radius,
                    rect.right, rect.top,
                    rect.right + diff, rect.top
            );
            path.lineTo(
                    prevRight - diff, rect.top
            );
            path.cubicTo(
                    prevRight - diff, rect.top,
                    prevRight, rect.top,
                    prevRight, prevBottom - SpotTextRenderView.radius
            );
            path.cubicTo(
                    prevRight, prevBottom - SpotTextRenderView.radius,
                    prevRight, prevBottom,
                    prevRight - SpotTextRenderView.radius, prevBottom

            );
            path.lineTo(
                    prevLeft + SpotTextRenderView.radius, prevBottom
            );
            path.cubicTo(
                    prevLeft + SpotTextRenderView.radius, prevBottom,
                    prevLeft, prevBottom,
                    prevLeft, rect.top - SpotTextRenderView.radius
            );
            c.drawPath(path, paintStroke);
        }

        prevWidth = width;
        prevLeft = rect.left;
        prevRight = rect.right;
        prevBottom = rect.bottom;
        prevTop = rect.top;
    }
}
