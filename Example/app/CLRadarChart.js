import React from 'react';
import { AppRegistry, StyleSheet, Text, View, processColor } from 'react-native';
import update from 'immutability-helper';
import { CircularRadarChart } from 'react-native-charts-wrapper';
import { widthPercentageToDP } from 'react-native-responsive-screen';
import Profiler from 'Profiler';

import {
  MATHEMATICS,
  SCIENCE,
  SINHALA,
  HISTORY,
  ICT,
  ENGLISH,
  ALL,
  CHART_RESET_ANIMATION_FACTOR,
  CHART_DURATION_Y,
  CHART_EASING_Y,
  NO_CHART_DATA_MSG
} from '../../../global/Constants';
import { DotSpinner } from '../../common';

const TAG = 'CLRadarChart';

class CLRadarChart extends React.Component {
  constructor(props) {
    super(props);
    //Profiler(this); // Add this line
    this.state = {
      loading: false,
      resetted: false,
      chartValuesArray: [],
      data: {},
      fakeData: {},
      webColor: processColor('#6B6B6B'),
      webColorInner: processColor('#6B6B6B'),
      animation: {
        durationX: 0,
        durationY: CHART_DURATION_Y,
        easingY: CHART_EASING_Y
      },
      xAxis: {
        axisMinimum: 9,
        position: 'BOTTOM',
        valueFormatter: [],
        textSize: 12,
        textColor: processColor('#6B6B6B00')
      },
      yAxis: {
        textColor: processColor('#6B6B6B'),
        drawLabels: false,
        axisMinimum: 0.0,
        axisMaximum: 100.0,
        labelCount: 5,
        labelCountForce: true,
        valueFormatter: 'percent'
      },
      legend: {
        enabled: false,
        textSize: 14,
        form: 'SQUARE',
        wordWrapEnabled: true
      }
    };
  }

  componentDidMount() {
    const { data } = this.props;
    this.updateChart(data);
  }

  shouldComponentUpdate(nextProps, nextState) {
    const chartValuesArray = this.state.chartValuesArray;
    const nextData = nextProps.data;
    let chartValuesArrayNext = [];

    if (!!nextData && !!nextData.defaultFill) {
      nextData.defaultFill.forEach(element => {
        chartValuesArrayNext.push(element.successRate);
      });

      if (!chartValuesArrayNext.length) {
        chartValuesArrayNext = [];
      }
    }

    if (
      JSON.stringify(chartValuesArrayNext) !== JSON.stringify(chartValuesArray) &&
      nextProps.subject !== 'ALL' &&
      (nextState.resetted || this.props.forceRender)
    ) {
      this.updateChart(nextData);
      return true;
    }

    return (
      (nextProps.subject !== this.props.subject && nextProps.subject !== 'ALL') ||
      nextState.resetted !== this.state.resetted ||
      this.props.forceRender
    );
  }

  UNSAFE_componentWillUpdate(nextProps, nextState) {
    if (nextProps.subject !== this.props.subject) {
      //console.log(TAG, nextProps.subject);
      //this.resetChart(nextProps.subject);
    }

    /* console.log(
      TAG,
      'will update',
      (nextProps.subject !== this.props.subject && nextProps.subject !== 'ALL') ||
        nextState.data !== this.state.data ||
        nextState.resetted !== this.state.resetted
    ); */
    //console.log(TAG, 'data', nextState.data, this.state.data);
    //console.log(TAG, 'resetted', nextState.resetted, this.state.resetted);
  }

  componentWillUnmount() {
    //if (this.timer) TimerMixin.clearTimeout(this.timer);
  }

  updateChart(data1) {
    console.warn(TAG, 'will update', this.state.resetted);

    if (!this.state.resetted && !this.props.forceRender) {
      return;
    }
    let data = null;

    if (!data1) {
      data = {
        values: [],
        labels: [],
        chartValuesArray: []
      };
    } else {
      data = { values: [], labels: [], chartValuesArray: [] };
      for (let index = 0; index < data1.defaultFill.length; index++) {
        const element = data1.defaultFill[index];
        data.values.push({ value: element.successRate });
        data.labels.push(element.category);
        data.chartValuesArray.push(element.successRate);
      }
      if (!data.chartValuesArray.length) {
        data.values = [];
        data.chartValuesArray = [];
        data.labels = [];
      }
    }
    const { chartStrokeColor, chartFillColor } = this.props.colors;
    this.setState(
      update(this.state, {
        chartValuesArray: {
          $set: data.chartValuesArray
        },
        webColor: { $set: processColor(chartFillColor) },
        webColorInner: { $set: processColor(chartFillColor) },
        data: {
          $set: {
            dataSets: [
              {
                values: data.values,
                label: 'DS 1',
                config: {
                  mode: 'CUBIC_BEZIER',
                  color: processColor(chartStrokeColor),
                  drawValues: true,
                  drawFilled: true,
                  fillColor: processColor(chartFillColor),
                  fillAlpha: 100,
                  lineWidth: 2
                }
              }
            ]
          }
        },
        xAxis: {
          $set: {
            axisMinimum: 9,
            valueFormatter: data.labels,
            textSize: 14,
            textColor: processColor(chartStrokeColor)
          }
        },
        animation: {
          $set: {
            durationX: 0,
            durationY: CHART_DURATION_Y,
            easingY: CHART_EASING_Y,
            random: Math.random()
          }
        },
        resetted: { $set: false },
        loading: { $set: false }
      })
    );
  }

  resetChart(subject) {
    //console.log(TAG, 'resetChart');
    //const t = new Date().getTime();
    //console.log(TAG, 'resetChart', t);
    console.log(TAG, 'updated', !this.state.resetted);
    if (!this.state.resetted) {
      const newDuration =
        this.state.animation.durationY > 0
          ? -(this.state.animation.durationY / CHART_RESET_ANIMATION_FACTOR)
          : -(this.state.animation.durationY * CHART_RESET_ANIMATION_FACTOR);
      //console.log(TAG, 'resetChart', newDuration);
      this.setState(
        update(this.state, {
          ///key: { $set: Math.random() },
          animation: {
            $set: {
              //durationX: 0,
              durationY: newDuration,
              easingY: CHART_EASING_Y,
              random: Math.random()
            }
          },
          xAxis: {
            $set: {
              ...this.state.xAxis,
              textColor: processColor('#6B6B6B00')
            }
          },
          chartValuesArray: { $set: [] },
          resetted: { $set: true },
          loading: { $set: true }
        })
      );
    } else {
      //this.props.resettingCompleted(subject);
    }
  }

  handleSelect(event) {
    const entry = event.nativeEvent;
    if (entry == null) {
      this.setState({ ...this.state, selectedEntry: null });
    } else {
      this.setState({ ...this.state, selectedEntry: JSON.stringify(entry) });
    }

    //console.log(event.nativeEvent);
  }

  renderProgress() {
    const { chartStrokeColor } = this.props.colors;
    if (this.state.loading) {
      return (
        <View style={styles.progresPositionStyle}>
          <DotSpinner color={chartStrokeColor} size={widthPercentageToDP('1%')} count={5} />
        </View>
      );
    } else if (this.state.chartValuesSum < 1) {
      return (
        <View style={styles.progresPositionStyle}>
          <Text style={{ color: chartStrokeColor }}>{NO_CHART_DATA_MSG}</Text>
        </View>
      );
    }
  }

  render() {
    //const t = new Date().getTime();
    //console.log(TAG, 'render', t);
    const { chartStrokeColor, chartFillColor } = this.props.colors;
    return (
      <View style={{ flex: 1 }}>
        <View style={styles.container} pointerEvents="none">
          <CircularRadarChart
            extraOffsets={{ left: 0, right: 0, top: 5, bottom: 25 }}
            animation={this.state.animation}
            style={styles.chart}
            data={this.state.data}
            xAxis={this.state.xAxis}
            yAxis={this.state.yAxis}
            chartDescription={{ text: '' }}
            legend={this.state.legend}
            drawWeb
            webLineWidth={1}
            webLineWidthInner={1}
            webAlpha={128}
            webColor={this.state.webColor}
            webColorInner={this.state.webColorInner}
            skipWebLineCount={0}
            //onSelect={this.handleSelect.bind(this)}
            //onChange={event => console.log(event.nativeEvent)}
            minOffset={0}
          />
        </View>
        {/* this.renderProgress() */}
      </View>
    );
  }
}

const top = widthPercentageToDP('50%') / 2 - 20;
const styles = StyleSheet.create({
  container: {
    justifyContent: 'center',
    alignContent: 'center',
    alignItems: 'center',
    alignSelf: 'center',
    flex: 1,
    //backgroundColor: 'red',
    borderColor: '#B1B1B1'
  },
  chart: {
    //position: 'absolute',
    alignSelf: 'center',
    alignContent: 'center',
    alignItems: 'center',
    //backgroundColor: 'red',
    height: widthPercentageToDP('60%'),
    width: widthPercentageToDP('75%')
  },

  progresPositionStyle: {
    position: 'absolute',
    alignSelf: 'center',
    top
  }
});

export default CLRadarChart;
