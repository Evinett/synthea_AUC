package org.mitre.synthea.helpers;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.mitre.synthea.world.agents.Person;

public class ChartRenderer {

  /** Enumeration of supported chart types. **/
  public enum ChartType {
    SCATTER,
    LINE
  }

  /**
   * POJO configuration for a chart.
   **/
  public abstract static class ChartConfig implements Serializable {
    /** Name of the image file to export. **/
    private String filename;
    /** User input for the type of chart to render. **/
    private String type;
    /** Chart title. **/
    private String title;
    /** X axis label. **/
    private String axisLabelX;
    /** Y axis label. **/
    private String axisLabelY;
    /** Chart width in pixels. **/
    private int width = 600;
    /** Chart height in pixels. **/
    private int height = 400;
    /** Whether to hide the X axis. **/
    private boolean axisHiddenX = false;
    /** Whether to hide the Y axis. **/
    private boolean axisHiddenY = false;

    public String getFilename() {
      return filename;
    }

    public void setFilename(String filename) {
      this.filename = filename;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getAxisLabelX() {
      return axisLabelX;
    }

    public void setAxisLabelX(String axisLabelX) {
      this.axisLabelX = axisLabelX;
    }

    public String getAxisLabelY() {
      return axisLabelY;
    }

    public void setAxisLabelY(String axisLabelY) {
      this.axisLabelY = axisLabelY;
    }

    public int getWidth() {
      return width;
    }

    public void setWidth(int width) {
      this.width = width;
    }

    public int getHeight() {
      return height;
    }

    public void setHeight(int height) {
      this.height = height;
    }

    public boolean isAxisHiddenX() {
      return axisHiddenX;
    }

    public void setAxisHiddenX(boolean axisHiddenX) {
      this.axisHiddenX = axisHiddenX;
    }

    public boolean isAxisHiddenY() {
      return axisHiddenY;
    }

    public void setAxisHiddenY(boolean axisHiddenY) {
      this.axisHiddenY = axisHiddenY;
    }
  }

  // MultiTableChartConfig class disabled - Physiology feature removed

  /**
   * POJO configuration for a chart with data from a Person object.
   **/
  public static class PersonChartConfig extends ChartConfig implements Serializable {
    /** Person attribute to render on the x axis. **/
    private String axisAttributeX;
    /** List of series configurations for this chart. **/
    private List<PersonSeriesConfig> series;

    public String getAxisAttributeX() {
      return axisAttributeX;
    }

    public void setAxisAttributeX(String axisAttributeX) {
      this.axisAttributeX = axisAttributeX;
    }

    public List<PersonSeriesConfig> getSeries() {
      return series;
    }

    public void setSeries(List<PersonSeriesConfig> series) {
      this.series = series;
    }
  }

  /**
   * POJO configuration for a chart series.
   **/
  public abstract static class SeriesConfig implements Serializable {
    /** Label for this series. **/
    private String label;

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }
  }

  // MultiTableSeriesConfig class disabled - Physiology feature removed

  /**
   * POJO configuration for a chart series with data from a Person object.
   */
  public static class PersonSeriesConfig extends SeriesConfig implements Serializable {
    /** Which attribute to plot on this series. (if providing a Person) **/
    private String attribute;

    public String getAttribute() {
      return attribute;
    }

    public void setAttribute(String attr) {
      this.attribute = attr;
    }
  }

  // MultiTable createChart method disabled - Physiology feature removed

  /**
   * Create a JFreeChart object based on values from a Person object.
   * @param person Person instance to retrieve values from
   * @param config chart configuration options
   */
  @SuppressWarnings("unchecked")
  public static JFreeChart createChart(Person person, PersonChartConfig config) {

    // If no chart type was provided, throw an exception
    if (config.getType() == null) {
      throw new IllegalArgumentException("Chart type must be provided");
    }

    // Create the dataset
    XYSeriesCollection dataset = new XYSeriesCollection();

    // Get the list of x values. Time is treated specially since it doesn't have an attribute identifier
    boolean axisXIsTime = "time".equalsIgnoreCase(config.getAxisAttributeX());
    List<Double> valuesX = null;

    // Add each series to the dataset
    for (PersonSeriesConfig seriesConfig : config.getSeries()) {
      // If a label is not provided, use the attribute as the label
      String seriesLabel = seriesConfig.getLabel();
      if (seriesLabel == null || seriesLabel.isEmpty()) {
        seriesLabel = seriesConfig.getAttribute();
      }

      XYSeries series = new XYSeries(seriesLabel);

      // Get the attribute value for this series
      Object seriesObject = person.attributes.get(seriesConfig.getAttribute());
      if (seriesObject == null) {
        throw new IllegalArgumentException("Person does not have attribute \""
            + seriesConfig.getAttribute() + "\"");
      }

      List<Double> seriesValues = null;

      // Handle the x axis values
      if (axisXIsTime && valuesX == null) {
        // If the x axis is time, we need to get the time values from the first series
        if (seriesObject instanceof org.mitre.synthea.helpers.TimeSeriesData) {
          org.mitre.synthea.helpers.TimeSeriesData timeSeries = 
              (org.mitre.synthea.helpers.TimeSeriesData) seriesObject;
          valuesX = new ArrayList<Double>(timeSeries.getValues().size());
          for (int i = 0; i < timeSeries.getValues().size(); i++) {
            valuesX.add(timeSeries.getPeriod() * i);
          }
        }
      } else if (seriesConfig.getAttribute().equals(config.getAxisAttributeX())) {
        // This series is the x axis
        if (seriesObject instanceof org.mitre.synthea.helpers.TimeSeriesData) {
          org.mitre.synthea.helpers.TimeSeriesData timeSeries = 
              (org.mitre.synthea.helpers.TimeSeriesData) seriesObject;
          valuesX = new ArrayList<Double>(timeSeries.getValues().size());
          for (int i = 0; i < timeSeries.getValues().size(); i++) {
            valuesX.add(timeSeries.getPeriod() * i);
          }
        }
      } else if (seriesObject instanceof List
          && ((List<?>) seriesObject).get(0) instanceof Double) {
        seriesValues = (List<Double>) seriesObject;
      } else {
        throw new RuntimeException("Invalid Person attribute \""
            + seriesConfig.getAttribute() + "\" provided for chart series: "
            + seriesObject + ". Attribute value must be a TimeSeriesData or List<Double> Object.");
      }

      if (valuesX == null) {
        throw new RuntimeException("When the special attribute \"time\" is provided for the X axis,"
            + " the first series attribute MUST point to a valid TimeSeriesData object.");
      }

      Iterator<Double> iterX = valuesX.iterator();
      Iterator<Double> seriesIter = seriesValues.iterator();

      while (iterX.hasNext()) {
        if (!seriesIter.hasNext()) {
          throw new RuntimeException("List for attribute \"" + seriesConfig.getAttribute()
              + "\" does not have the same length as the x axis values \""
              + config.getAxisAttributeX() + "\"");
        }
        series.add(iterX.next(), seriesIter.next());
      }

      dataset.addSeries(series);
    }

    // Instantiate our renderer to draw the chart
    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    JFreeChart chart;

    // Determine the appropriate Chart from the configuration options
    switch (ChartType.valueOf(config.getType().toUpperCase())) {
      default:
      case LINE:
        chart = ChartFactory.createXYLineChart(
            config.getTitle(),
            config.getAxisLabelX(),
            config.getAxisLabelY(),
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
          renderer.setSeriesShapesVisible(i, false);
        }
        break;
      case SCATTER:
        chart = ChartFactory.createScatterPlot(
            config.getTitle(),
            config.getAxisLabelX(),
            config.getAxisLabelY(),
            dataset
        );
        break;
    }

    // If there's only one series, and there's a chart title, the legend is unnecessary
    if (config.getTitle() != null && !config.getTitle().isEmpty()
        && config.getSeries().size() == 1) {
      chart.removeLegend();
    } else {
      chart.getLegend().setFrame(BlockBorder.NONE);
    }

    // Instantiate the plot and set some reasonable styles
    // TODO eventually we can make these more configurable if desired
    XYPlot plot = chart.getXYPlot();

    if (config.isAxisHiddenX()) {
      plot.getDomainAxis().setVisible(false);
    }

    if (config.isAxisHiddenY()) {
      plot.getRangeAxis().setVisible(false);
    }

    plot.setRenderer(renderer);
    plot.setBackgroundPaint(Color.white);
    plot.setRangeGridlinesVisible(true);
    plot.setDomainGridlinesVisible(true);

    return chart;
  }

  // MultiTable drawChartAsFile method disabled - Physiology feature removed

  /**
   * Draw a JFreeChart to an image based on values from a MultiTable.
   * @param person Person to retrieve attributes from
   * @param config chart configuration options
   */
  public static void drawChartAsFile(Person person, PersonChartConfig config) {

    JFreeChart chart = createChart(person, config);

    // Save the chart as a PNG image to the file system
    try {
      ChartUtils.saveChartAsPNG(new File(config.getFilename()), chart, config.getWidth(),
          config.getHeight());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * POJO for holding a base64 encoded chart.
   **/
  public static class Base64EncodedChart implements Serializable {
    private String encodedBytes;
    private int unencodedLength;

    public Base64EncodedChart(byte[] bytes) {
      this.encodedBytes = new String(Base64.getEncoder().encode(bytes));
      this.unencodedLength = bytes.length;
    }

    public String getEncodedBytes() {
      return encodedBytes;
    }

    public int getUnencodedLength() {
      return unencodedLength;
    }
  }

  // MultiTable drawChartAsBase64 method disabled - Physiology feature removed

  /**
   * Draw a JFreeChart to a base64 encoded image based on values from a MultiTable.
   * @param person Person to retrieve attribute values from
   * @param config chart configuration options
   */
  public static Base64EncodedChart drawChartAsBase64(Person person, PersonChartConfig config)
      throws IOException {

    JFreeChart chart = createChart(person, config);

    byte[] imgBytes = ChartUtils.encodeAsPNG(chart.createBufferedImage(config.getWidth(),
        config.getHeight()));
    return new Base64EncodedChart(imgBytes);
  }
}