package org.planningpoker.wicket.cardimage;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.apache.wicket.markup.html.image.resource.RenderedDynamicImageResource;
import org.planningpoker.domain.ICard;

class CardImageResource extends RenderedDynamicImageResource {
	private static final long serialVersionUID = 1L;

	private static final int STANDARD_HEIGHT = 60;
	private static final int STANDARD_WIDTH = 40;

	private final ICard card;

	private final float margin;

	private final float textWidth;

	public CardImageResource(ICard card) {
		this(STANDARD_WIDTH, STANDARD_HEIGHT, card);
	}

	public CardImageResource(double scale, ICard card) {
		this((int) Math.round(STANDARD_WIDTH * scale), (int) Math
				.round(STANDARD_HEIGHT * scale), card);
	}

	public CardImageResource(int width, int height, ICard card) {
		super(width, height, "png");
		this.card = card;

		margin = (float) ((double) getWidth() / (double) 8);
		textWidth = (float) ((double) width - margin * 2);
	}

	@Override
	protected boolean render(Graphics2D graphics) {
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		graphics.setColor(Color.YELLOW);
		int round = (int) Math.round((double) getWidth() / (double) 4);
		graphics.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, round,
				round);
		graphics.setColor(Color.BLACK);
		graphics.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, round,
				round);

		if (card != null) {
			drawCard(graphics);
		}

		return true;
	}

	private void drawCard(Graphics2D graphics) {
		Font font = graphics.getFont();
		try {
			font = Font.createFont(Font.TYPE1_FONT, CardImageResource.class
					.getResourceAsStream("KontrBol.pfb"));
			font = font.deriveFont(24f);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		graphics.setFont(font);

		setFontSize(graphics, card.getDisplayValue());
	}

	private Rectangle2D setFontSize(Graphics2D graphics, String text) {
		FontMetrics fontMetrics = graphics.getFontMetrics();
		Rectangle2D stringBounds = fontMetrics.getStringBounds(text, graphics);

		while (fontMetrics.stringWidth(text) < textWidth - 1
				|| fontMetrics.stringWidth(text) > textWidth + 1) {
			double height = stringBounds.getHeight();
			double width = stringBounds.getWidth();

			float newSize = (float) height / (float) width;

			graphics
					.setFont(graphics.getFont().deriveFont(textWidth * newSize));

			fontMetrics = graphics.getFontMetrics();
			stringBounds = fontMetrics.getStringBounds(text, graphics);
		}

		float textY = (float) ((getHeight() - stringBounds.getHeight()) / 2 - stringBounds
				.getMinY());

		// graphics.setColor(Color.YELLOW);
		// graphics.fill(new Rectangle2D.Float(5f,
		// (float) (getHeight() - stringBounds.getHeight()) / 2,
		// (float) stringBounds.getWidth(), (float) stringBounds
		// .getHeight()));

		graphics.setColor(Color.BLACK);
		// LineMetrics lineMetrics = fontMetrics.getLineMetrics(text, graphics);
		// float y = (lineMetrics.getDescent() + textY);
		// graphics.draw(new Line2D.Float(0f, y, getWidth(), y));

		graphics.drawString(card.getDisplayValue(), margin, textY);

		return stringBounds;
	}
}
