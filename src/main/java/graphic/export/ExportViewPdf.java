package graphic.export;

import graphic.GraphicView;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.commons.io.input.XmlStreamReader;
import org.apache.fop.svg.PDFTranscoder;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Function;

public final class ExportViewPdf extends ExportViewVectorFile {

  public static ExportViewPdf create(final GraphicView graphicView, final File file, final boolean displayTitle) {
    return new ExportViewPdf(graphicView, file, displayTitle);
  }

  public static ExportViewPdf create(final GraphicView graphicView, final File file) {
    return create(graphicView, file, graphicView.getTxtBoxDiagramName().isVisible());
  }

  private ExportViewPdf(final GraphicView graphicView, final File file, final boolean displayTitle) {
    super(graphicView, file, displayTitle);
  }

  @Override
  protected Rectangle getOuterBounds() {
    return new Rectangle(
        0,
        0,
        bounds.x + bounds.width + MARGIN,
        bounds.y + bounds.height + MARGIN);
  }

  @Override
  protected void writeToFile(final FileOutputStream fileOutputStream,
                             final Function<SVGGraphics2D, SVGGraphics2D> draw) throws Exception {

    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         Writer writer = new OutputStreamWriter(byteArrayOutputStream)) {
      ExportViewSvg.createSVG(this, draw).stream(writer);
      writer.flush();

      try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
           Reader reader = new XmlStreamReader(byteArrayInputStream)) {
        TranscoderInput transcoderInput = new TranscoderInput(reader);
        TranscoderOutput transcoderOutput = new TranscoderOutput(fileOutputStream);
        Transcoder transcoder = new PDFTranscoder();
        transcoder.transcode(transcoderInput, transcoderOutput);
      }
    }

  }

}
