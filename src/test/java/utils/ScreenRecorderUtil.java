package utils;

import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

public class ScreenRecorderUtil extends ScreenRecorder {

	public static ScreenRecorder screenRecorder;
	private String name;

	public ScreenRecorderUtil(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat,
			Format screenFormat, Format mouseFormat, Format audioFormat, File movieFolder, String name)
			throws IOException, AWTException {
		super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
		this.name = name;
	}

	@Override
	protected File createMovieFile(Format fileFormat) throws IOException {
		if (!movieFolder.exists()) {
			movieFolder.mkdirs();
		} else if (!movieFolder.isDirectory()) {
			throw new IOException("\"" + movieFolder + "\" is not a directory.");
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return new File(movieFolder,
				name + "_" + dateFormat.format(new Date()) + "." + Registry.getInstance().getExtension(fileFormat));
	}

	/**
	 * Starts recording the screen for the current scenario/test.
	 *
	 * @param scenarioName Name or title of the scenario being executed.
	 */
	public static void startRecord(String scenarioName) {
		try {
			File file = new File("./recordings/");
			if (!file.exists()) {
				file.mkdirs();
			}

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int width = screenSize.width;
			int height = screenSize.height;

			Rectangle captureSize = new Rectangle(0, 0, width, height);

			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice().getDefaultConfiguration();

			String safeName = scenarioName.replaceAll("[^a-zA-Z0-9_\\-]", "_");

			screenRecorder = new ScreenRecorderUtil(gc, captureSize,
					new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
					new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_MJPG,
							CompressorNameKey, ENCODING_AVI_MJPG,
							DepthKey, 24, FrameRateKey, Rational.valueOf(10),
							QualityKey, 0.75f,
							KeyFrameIntervalKey, 15 * 60),
					new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(20)),
					null, file, safeName);

			screenRecorder.start();
			System.out.println("==================================================");
			System.out.println(" [SCREEN RECORDING STARTED] Scenario: " + scenarioName);
			System.out.println(" - Output Directory: " + file.getAbsolutePath());
			System.out.println("==================================================");
		} catch (Exception e) {
			System.err.println("Failed to start screen recording: " + e.getMessage());
		}
	}

	/**
	 * Stops the screen recording and saves the video file to ./recordings/
	 */
	public static void stopRecord() {
		try {
			if (screenRecorder != null) {
				screenRecorder.stop();
				System.out.println("==================================================");
				System.out.println(" [SCREEN RECORDING COMPLETED] Saved to ./recordings/");
				System.out.println("==================================================");
			}
		} catch (Exception e) {
			System.err.println("Failed to stop screen recording: " + e.getMessage());
		}
	}
}
