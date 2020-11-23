var wavesurfer;

function initPlayer(filesToShow) {

    'use strict';

    // Create an instance
    wavesurfer = Object.create(WaveSurfer);

    // Init & load audio file
    var options = {
        container: document.querySelector('#waveform'),
        waveColor: 'violet',
        progressColor: 'purple',
        cursorColor: 'navy',
        normalize: true,
        minimap: true,
        pixelRatio: 1,
        minPxPerSec: 80,
        scrollParent: true,
    };

    // Init
    wavesurfer.init(options);
    
    // Load audio from URL
//    wavesurfer.load(fileToPlay);
    wavesurfer.loadFiles(0, filesToShow);

    // Regions
    if (wavesurfer.enableDragSelection) {
        wavesurfer.enableDragSelection({
            color: 'rgba(0, 255, 0, 0.1)'
        });
    }

    //calculate height for all waveforms
    calculateWaveformHeights();

    /* init stuff */
    wavesurfer.on('ready', function () {
        // timeline
        var timeline = Object.create(WaveSurfer.Timeline);
        timeline.init({
            wavesurfer: wavesurfer,
            container: "#wave-timeline"
        });
////        var regions = extractRegions(
////                        wavesurfer.backend.getPeaks(512),
////                        wavesurfer.getDuration()
////                        );
////        loadRegions(regions);
        loadTranscribedRegions(getTranscribedData());
        stickGestureData();
        stickSoundData();
        showUsernameAtWaveforms();
    });
    
    /* region */
    wavesurfer.on('region-click', function (region, e) {
        currRegion = region;
        e.stopPropagation();
        $("#note").blur();
        $("#note").focus();
    });
    wavesurfer.on('region-click', editAnnotation);
    wavesurfer.on('region-updated', editAnnotation);
    wavesurfer.on('region-in', showNote);
    wavesurfer.on('region-out', showEmptyNote);
    wavesurfer.on('region-play', function (region) {
        region.once('out', function () {
            wavesurfer.play(region.start);
            wavesurfer.pause();
        });
    });

    /* Minimap plugin */
//    wavesurfer.initMinimap({
//        height: 30,
//        waveColor: '#ddd',
//        progressColor: '#999',
//        cursorColor: '#999'
//    });

    // Report errors
    wavesurfer.on('error', function (err) {
        console.error(err);
    });

    // Do something when the clip is over
    wavesurfer.on('finish', function () {
        console.log('Finished playing');
    });


    /* Progress bar */
    var progressDiv = document.querySelector('#progress-bar');
    var progressBar = progressDiv.querySelector('.progress-bar');

    var showProgress = function (percent) {
        progressDiv.style.display = 'block';
        progressBar.style.width = percent + '%';
    };

    var hideProgress = function () {
        progressDiv.style.display = 'none';
    };
    wavesurfer.on('loading', showProgress);
    wavesurfer.on('ready', hideProgress);
    wavesurfer.on('destroy', hideProgress);
    wavesurfer.on('error', hideProgress);
    
    /* Toggle play/pause buttons. */
    var playButton = document.querySelector('#play');
    var pauseButton = document.querySelector('#pause');
    wavesurfer.on('play', function () {
        playButton.style.display = 'none';
        pauseButton.style.display = '';
    });
    wavesurfer.on('pause', function () {
        playButton.style.display = '';
        pauseButton.style.display = 'none';
    });
}

/**
 * Save annotations to localStorage.
 */
//function saveRegions() {
////    localStorage.regions = JSON.stringify(
////            Object.keys(wavesurfer.regions.list).map(function (id) {
////        var region = wavesurfer.regions.list[id];
////        return {
////            start: region.start,
////            end: region.end,
////            attributes: region.attributes,
////            data: region.data
////        };
////    })
////    );
//    console.log("saveRegions...");
//    saveTranscription();
//}


/**
 * Load regions from localStorage.
 */
function loadRegions(regions) {
    regions.forEach(function (region) {
        region.color = "rgba(0, 255, 0, 0.0980392)";
        wavesurfer.addRegion(region);
    });
}
function loadGestureRegions(regions,colorIndex,resize,drag,height,editable,zIndex,addLabel) {
    regions.forEach(function (region) {
        region.color = colorsMedAlpha[colorIndex];
        region.borderColor = colors[colorIndex];
        region.resize = resize;
        region.drag = drag;
        region.height = height;
        region.editable = editable;
        region.zIndex = zIndex;
        region.addLabel = addLabel;
        region.forWhat = "Gesture";
        wavesurfer.addRegion(region);
    });
}
function loadSoundRegions(regions,colorIndex,resize,drag,height,editable,zIndex,addLabel) {
    regions.forEach(function (region) {
        region.color = colorsMedAlpha[colorIndex];
        region.borderColor = colors[colorIndex];
        region.resize = resize;
        region.drag = drag;
        region.height = height;
        region.editable = editable;
        region.zIndex = zIndex;
        region.addLabel = addLabel;
        region.forWhat = "Sound";
        wavesurfer.addRegion(region);
    });
}

function loadTranscribedRegions(regions) {
    regions.forEach(function (region) {
        var colorIndex = parseInt(region.top/waveformHeight);
        if (colorIndex !== 0) {
            colorIndex = colorIndex % 5;
        }
        region.color = colorsLowAlpha[colorIndex];
        region.borderColor = colors[colorIndex];
        region.resize = true;
        region.drag = true;
        region.height = "25px";
        region.bottom = "80px";
        region.editable = true;
        region.zIndex = "2";
        region.addLabel = true;
        region.forWhat = "Transcription";
        wavesurfer.addRegion(region);
    });
}


/**
 * Extract regions separated by silence.
 */
function extractRegions(peaks, duration) {
    // Silence params
    var minValue = 0.03;
    var minSeconds = 0.25;

    var length = peaks.length;
    var coef = duration / length;
    var minLen = minSeconds / coef;
    
    var newPeaks = [];
    var idx = 0;
    for(var v in peaks) {
        peaks[v] = Math.round(peaks[v] * 1000) / 1000;
        idx++;
    }


    var frequency = {};  // array of frequency.
    var max = 0;  // holds the max frequency.
    var result;   // holds the max frequency element.
    for(var v in peaks) {
            frequency[peaks[v]]=(frequency[peaks[v]] || 0)+1; // increment frequency.
            if(frequency[peaks[v]] > max) { // is this frequency > max so far ?
                    max = frequency[peaks[v]];  // update max.
                    result = peaks[v];          // update result.
            }
    }
    minValue = result;

    // Gather silence indeces
    var silences = [];
    Array.prototype.forEach.call(peaks, function (val, index) {
        if (Math.abs(val) < minValue) {
            silences.push(index);
        }
    });

    // Cluster silence values
    var clusters = [];
    silences.forEach(function (val, index) {
        if (clusters.length && val == silences[index - 1] + 1) {
            clusters[clusters.length - 1].push(val);
        } else {
            clusters.push([val]);
        }
    });

    // Filter silence clusters by minimum length
    var fClusters = clusters.filter(function (cluster) {
        return cluster.length >= minLen;
    });

    // Create regions on the edges of silences
    var regions = fClusters.map(function (cluster, index) {
        var next = fClusters[index + 1];
        return {
            start: cluster[cluster.length - 1],
            end: (next ? next[0] : length - 1)
        };
    });

    // Add an initial region if the audio doesn't start with silence
    var firstCluster = fClusters[0];
    if (firstCluster && firstCluster[0] != 0) {
        regions.unshift({
            start: 0,
            end: firstCluster[firstCluster.length - 1]
        });
    }

    // Filter regions by minimum length
    var fRegions = regions.filter(function (reg) {
        return reg.end - reg.start >= minLen;
    });

    // Return time-based regions
    return fRegions.map(function (reg) {
        return {
            start: Math.round(reg.start * coef * 10) / 10,
            end: Math.round(reg.end * coef * 10) / 10
        };
    });
}


/**
 * Random RGBA color.
 */
function randomColor(alpha) {
    return 'rgba(' + [
        ~~(Math.random() * 255),
        ~~(Math.random() * 255),
        ~~(Math.random() * 255),
        alpha || 1
    ] + ')';
}


/**
 * Edit annotation for a region.
 */
function editAnnotation(region) {
    currRegion = region;
    var form = document.forms.edit;
    form.style.opacity = 1;
    form.elements.start.value = Math.round(region.start * 10) / 10;
    form.elements.end.value = Math.round(region.end * 10) / 10;
    form.elements.top.value = region.top;
    form.elements.note.value = region.data.note || '';
    $("#transcription-id").val(region.id);
    form.onsubmit = function (e) {
        saveTranscription();
        e.preventDefault();
        region.update({
            start: form.elements.start.value,
            end: form.elements.end.value,
            top: form.elements.top.value,
            data: {
                note: form.elements.note.value
            }
        });
        //form.style.opacity = 0;
    };
    form.onreset = function () {
        form.style.opacity = 0;
        form.dataset.region = null;
    };
    form.dataset.region = region.id;
    
    form.elements.start.onchange = 
            form.elements.end.onchange = 
            form.elements.note.onchange = function(e) {
                form.onsubmit(e);
    };
}


/**
 * Display annotation.
 */
function showNote(region) {
//    if (!showNote.el) {
//        showNote.el = document.querySelector('#subtitle');
//    }
//    showNote.el.textContent = region.data.note || '–';
}

function showEmptyNote(region) {
//    if (!showNote.el) {
//        showNote.el = document.querySelector('#subtitle');
//    }
//    showNote.el.textContent = '-';
}

/**
 * Bind controls.
 */
GLOBAL_ACTIONS['delete-region'] = function () {
    alert(" | "+wavesurfer.regions);
    var form = document.forms.edit;
    var regionId = form.dataset.region;
    alert(" | "+regionId);
    if (regionId) {
        wavesurfer.regions.list[regionId].remove();
        form.reset();
    }
};

GLOBAL_ACTIONS['export'] = function () {
    window.open('data:application/json;charset=utf-8,' +
            encodeURIComponent(localStorage.regions));
};

/**
 * get length of text
 * @returns {Number}
 */
function getWidthOfText(fontSize, text) {
    var tmp = document.createElement("label");
    tmp.innerHTML = text;
    tmp.style.fontSize = fontSize;
    document.body.appendChild(tmp);
    var theWidth = tmp.getBoundingClientRect().width;
    document.body.removeChild(tmp);
    return theWidth;
}

/**
 * calculate top for a transcrition
 * @param {type} currentTop
 * @returns {Number}
 */
function calculateTopForTranscription(currentTop) {
    var currHeight = 0;
    for (var h = 0; h < waveformsHeight.length; h++) {
        if (currentTop < (currHeight + waveformsHeight[h])) {
            if(h===0) {
                return currHeight+20;
            } else {
                return currHeight;
            }
        }
        currHeight = currHeight + waveformsHeight[h];
    }
}

/**
 * get on which index the clicked happen
 * @param {type} currentTop
 * @returns {Number}
 */
function getIndexForWaveform(currentTop) {
    var currHeight = 0;
    for (var h = 0; h < waveformsHeight.length; h++) {
        if (currentTop < (currHeight + waveformsHeight[h])) {
            return h;
        }
        currHeight = currHeight + waveformsHeight[h];
    }
}