'use strict';

/* Regions manager */
WaveSurfer.Regions = {
    init: function (wavesurfer) {
        this.wavesurfer = wavesurfer;
        this.wrapper = this.wavesurfer.drawer.wrapper;

        /* Id-based hash of regions. */
        this.list = {};
    },

    /* Remove a region. */
    add: function (params) {
        var region = Object.create(WaveSurfer.Region);
        region.init(params, this.wavesurfer);

        this.list[region.id] = region;

        region.on('remove', (function () {
            delete this.list[region.id];
        }).bind(this));

        return region;
    },

    /* Remove all regions. */
    clear: function () {
        Object.keys(this.list).forEach(function (id) {
            this.list[id].remove();
        }, this);
    },

    enableDragSelection: function (params) {
        var my = this;
        var drag;
        var start;
        var top;
        var region;

        this.wrapper.addEventListener('mousedown', function (e) {
            if(e.target.tagName!=="WAVE") {
                drag = true;
                var lefttop = my.wavesurfer.drawer.handleEventNew(e);
                start = lefttop.left;
                top = calculateTopForTranscription(lefttop.top);
                region = null;
                $("#note").blur();
            }
        });
        this.wrapper.addEventListener('mouseup', function (e) {
            if(e.target.tagName!=="WAVE") {
                drag = false;

                if (region) {
                    region.fireEvent('update-end', e);
                    my.wavesurfer.fireEvent('region-update-end', region, e);
                }

                region = null;
                $("#note").focus();
            }
        });
        this.wrapper.addEventListener('mousemove', function (e) {
            if (!drag) { return; }

            if (!region) {
                region = my.add(params || {});
            }

            var duration = my.wavesurfer.getDuration();
            var lefttop = my.wavesurfer.drawer.handleEventNew(e);
            var end = lefttop.left;
            var top = calculateTopForTranscription(lefttop.top);
            var index = getIndexForWaveform(lefttop.top);
            if (index !== 0) {
                index = index % 5;
            }
            region.update({
                start: Math.min(end * duration, start * duration),
                end: Math.max(end * duration, start * duration),
                color : colorsLowAlpha[index],
                borderColor : colors[index],
                resize : true,
                drag : true,
                height : "25px",
                top : top,
                editable : true,
                zIndex : "2",
                addLabel : true,
                forWhat : "Transcription"
            });
        });
    }
};

WaveSurfer.Region = {
    /* Helper function to assign CSS styles. */
    style: WaveSurfer.Drawer.style,

    init: function (params, wavesurfer) {
        this.wavesurfer = wavesurfer;
        this.wrapper = wavesurfer.drawer.wrapper;

        this.id = params.id == null ? WaveSurfer.util.getId() : params.id;
        this.start = Number(params.start) || 0;
        this.end = params.end == null ?
            // small marker-like region
            this.start + (4 / this.wrapper.scrollWidth) * this.wavesurfer.getDuration() :
            Number(params.end);
        this.resize = params.resize === undefined ? true : Boolean(params.resize);
        this.drag = params.drag === undefined ? true : Boolean(params.drag);
        this.loop = Boolean(params.loop);
        this.color = params.color || 'rgba(0, 0, 0, 0.1)';
        this.borderColor = params.borderColor || 'rgba(0, 0, 0, 0.1)';
        this.height = params.height || '100%';
        this.top = params.top || '0';
        this.forWhat = params.forWhat;
        this.zIndex = params.zIndex || '2';
        this.editable = params.editable === undefined ? true : Boolean(params.editable);
        this.addLabel = params.addLabel === undefined ? false : Boolean(params.addLabel);
        this.data = params.data || {};
        this.attributes = params.attributes || {};

        this.maxLength = params.maxLength;
        this.minLength = params.minLength;

        this.bindInOut();
        this.render();

        this.wavesurfer.fireEvent('region-created', this);
    },

    /* Update region params. */
    update: function (params) {
        if (null != params.start) {
            this.start = Number(params.start);
        }
        if (null != params.end) {
            this.end = Number(params.end);
        }
        if (null != params.loop) {
            this.loop = Boolean(params.loop);
        }
        if (null != params.color) {
            this.color = params.color;
        }
        if (null != params.borderColor) {
            this.borderColor = params.borderColor;
        }
        if (null != params.height) {
            this.height = params.height;
        }
        if (null != params.forWhat) {
            this.forWhat = params.forWhat;
        }
        if (null != params.top) {
            this.top = params.top;
        }
        if (null != params.zIndex) {
            this.zIndex = params.zIndex;
        }
        if (null != params.editable) {
            this.editable = params.editable;
        }
        if (null != params.addLabel) {
            this.addLabel = params.addLabel;
        }
        if (null != params.data) {
            this.data = params.data;
        }
        if (null != params.resize) {
            this.resize = Boolean(params.resize);
        }
        if (null != params.drag) {
            this.drag = Boolean(params.drag);
        }
        if (null != params.maxLength) {
            this.maxLength = Number(params.maxLength);
        }
        if (null != params.minLength) {
            this.minLength = Number(params.minLength);
        }
        if (null != params.attributes) {
            this.attributes = params.attributes;
        }

        this.updateRender();
        this.fireEvent('update');
        this.wavesurfer.fireEvent('region-updated', this);
    },

    /* Remove a single region. */
    remove: function (region) {
        if (this.element) {
            this.wrapper.removeChild(this.element);
            this.element = null;
            this.fireEvent('remove');
            this.wavesurfer.fireEvent('region-removed', this);
        }
    },

    /* Play the audio region. */
    play: function () {
        this.wavesurfer.play(this.start, this.end);
        this.fireEvent('play');
        this.wavesurfer.fireEvent('region-play', this);
    },

    /* Play the region in loop. */
    playLoop: function () {
        this.play();
        this.once('out', this.playLoop.bind(this));
    },

    /* Render a region as a DOM element. */
    render: function () {
        var regionEl = document.createElement('region');
        regionEl.className = 'wavesurfer-region';
        regionEl.title = this.formatTime(this.start, this.end, this.data.note);
        regionEl.setAttribute('data-id', this.id);

        for (var attrname in this.attributes) {
            regionEl.setAttribute('data-region-' + attrname, this.attributes[attrname]);
        }

        var width = this.wrapper.scrollWidth;
        if(!this.addLabel) {
            this.style(regionEl, {
                position: 'absolute',
                zIndex: this.zIndex,
                height: this.height,
                top: this.top+"px"
            });
        } else {
            if(this.forWhat==="Transcription") {
                this.style(regionEl, {
                    position: 'absolute',
                    zIndex: this.zIndex,
                    height: this.height,
                    'border-left': 'solid thick '+this.borderColor,
                    'border-right': 'solid thick '+this.borderColor,
                    top: this.top+"px"
                });
            } else if(this.forWhat==="Gesture") {
                this.style(regionEl, {
                    position: 'absolute',
                    zIndex: this.zIndex,
                    height: this.height,
                    'border-left': 'solid thick '+this.borderColor,
                    'border-right': 'solid thick '+this.borderColor,
                    top: this.top+"px"
                });
            } else if(this.forWhat==="Sound") {
                this.style(regionEl, {
                    position: 'absolute',
                    zIndex: this.zIndex,
                    height: this.height,
                    'border-left': 'solid thick '+this.borderColor,
                    'border-right': 'solid thick '+this.borderColor,
                    top: this.top+"px"
                });
            }
            
        }
        

        /* Resize handles */
        if (this.resize) {
            var handleLeft = regionEl.appendChild(document.createElement('handle'));
            var handleRight = regionEl.appendChild(document.createElement('handle'));
            handleLeft.className = 'wavesurfer-handle wavesurfer-handle-start';
            handleRight.className = 'wavesurfer-handle wavesurfer-handle-end';
            var css = {
                cursor: 'col-resize',
                position: 'absolute',
                left: '0px',
                top: '0px',
                width: '1%',
                maxWidth: '4px',
                height: this.height
            };
            this.style(handleLeft, css);
            this.style(handleRight, css);
            this.style(handleRight, {
                left: '100%'
            });
        }
        
        this.element = this.wrapper.appendChild(regionEl);
        this.updateRender();
        this.bindEvents(regionEl);
    },

    formatTime: function (start, end, note) {
        return (start == end ? [ start ] : [ start, end ]).map(function (time) {
            return [
                Math.floor((time % 3600) / 60), // minutes
                ('00' + Math.floor(time % 60)).slice(-2) // seconds
            ].join(':');
        }).join('–')+" "+note;
    },
    
    /* Update element's position, width, color, height. */
    updateRender: function () {
        var dur = this.wavesurfer.getDuration();
        var width = this.wrapper.scrollWidth;

        if (this.start < 0) {
          this.start = 0;
          this.end = this.end - this.start;
        }
        if (this.end > dur) {
          this.end = dur;
          this.start = dur - (this.end - this.start);
        }

        if (this.minLength != null) {
            this.end = Math.max(this.start + this.minLength, this.end);
        }

        if (this.maxLength != null) {
            this.end = Math.min(this.start + this.maxLength, this.end);
        }

        var borderStyle = "solid thick "+this.borderColor;
        if(this.forWhat==="Gesture" || this.forWhat==="Sound") {
            borderStyle = "solid thick "+this.borderColor;
        }
        this.style(this.element, {
            left: ~~(this.start / dur * width) + 'px',
            width: ~~((this.end - this.start) / dur * width) + 'px',
            backgroundColor: this.color,
            height : this.height,
            cursor: this.drag ? 'move' : 'default',
            top : this.top+"px",
            'border-left': borderStyle,
            'border-right': borderStyle,
            display: 'inline-table'
        });

        for (var attrname in this.attributes) {
            this.element.setAttribute('data-region-' + attrname, this.attributes[attrname]);
        }

        this.element.title = this.formatTime(this.start, this.end, this.data.note);
        
        if (null !== this.data) {
            if (typeof this.element.getElementsByTagName("label")[0] === 'undefined') {
                var labelText = this.element.appendChild(document.createElement('label'));
                var pos = "relative";
                if(typeof this.data.note === 'undefined') {
                    labelText.innerHTML = "";
                } else {
                    if(this.forWhat==="Gesture" || this.forWhat==="Sound") {
                        //repeat text if the sticker is long
                        var dur = this.wavesurfer.getDuration();
                        var width = this.wrapper.scrollWidth;
                        var finalWidth = ~~(this.end - this.start) * width / dur;
                        finalWidth = finalWidth - 22;
                        var textWidth = getWidthOfText("smaller", this.data.note);

                        var spaceToAdd = 600;
                        var spaceWidth = getWidthOfText("smaller", "&nbsp;");
                        var noOfSpace = spaceToAdd/spaceWidth;

                        for(var i=0;i<finalWidth;) {
                            var space = "";
                            for(var j=0;j<noOfSpace;j++) space = space+"&nbsp;";
                            if(i+textWidth+spaceToAdd>finalWidth) {
                                labelText.innerHTML = labelText.innerHTML + this.data.note;
                            } else {
                                labelText.innerHTML = labelText.innerHTML + this.data.note + space;
                            }
                            i=i+textWidth+spaceToAdd;
                        }
                        console.log("absolute...");
                        pos = "absolute";
                    } else {
                        labelText.innerHTML = this.data.note;
                    }
                }
                var labelTextCss = {
                    'font-size': 'smaller',
                    'vertical-align': 'top',
                    'width': '95%',
                    'padding-left': '12px',
                    'margin-bottom': '0px',
                    'cursor': this.drag ? 'move' : 'default',
                    'position':pos
                };
                var labelTextCss1 = {
                    'font-size': 'smaller',
                    'white-space': 'nowrap',
                    'overflow': 'hidden',
                    'text-overflow': 'ellipsis',
                    'display': 'inline-block',
                    'vertical-align': 'top',
                    'width': '95%',
                    'padding-left': '12px',
                    'margin-bottom': '0px',
                    'cursor': this.drag ? 'move' : 'default',
                    'position':pos
                };
                if(this.forWhat==="Sound") {
                    this.style(labelText, labelTextCss1);
                } else {
                    this.style(labelText, labelTextCss);
                }
                labelText.addEventListener("mousedown", function (e) {
                    if(this.element.forWhat==="Gesture" || this.element.forWhat==="Sound") {
                        return;
                    } else {
                        WaveSurfer.fireEvent('region-click', this.element, e);
                    }
                });
            } else {
                if(typeof this.data.note === 'undefined') {
                    this.element.getElementsByTagName("label")[0].innerHTML = "";
                } else {
                    this.element.getElementsByTagName("label")[0].innerHTML = this.data.note;
                }
            }
        }
    },
    
    /* Bind audio events. */
    bindInOut: function () {
        if(!this.editable)
            return;
        
        var my = this;

        var onPlay = function () {
            my.firedIn = false;
            my.firedOut = false;
        };

        var onProcess = function (time) {
            if (!my.firedIn && my.start <= time && my.end > time) {
                my.firedIn = true;
                my.fireEvent('in');
                my.wavesurfer.fireEvent('region-in', my);
            }
            if (!my.firedOut && my.firedIn && my.end <= Math.round(time * 100) / 100) {
                my.firedOut = true;
                my.fireEvent('out');
                my.wavesurfer.fireEvent('region-out', my);
            }
        };

        this.wavesurfer.on('play', onPlay);
        this.wavesurfer.backend.on('audioprocess', onProcess);

        this.on('remove', function () {
            my.wavesurfer.un('play', onPlay);
            my.wavesurfer.backend.un('audioprocess', onProcess);
        });

        /* Loop playback. */
        this.on('out', function () {
            if (my.loop) {
                my.wavesurfer.play(my.start);
            }
        });
    },

    /* Bind DOM events. */
    bindEvents: function () {
        if(!this.editable)
            return;
        
        var my = this;

        this.element.addEventListener('mouseenter', function (e) {
            my.fireEvent('mouseenter', e);
            my.wavesurfer.fireEvent('region-mouseenter', my, e);
        });
        
        this.element.addEventListener('mouseleave', function (e) {
            my.fireEvent('mouseleave', e);
            my.wavesurfer.fireEvent('region-mouseleave', my, e);
        });

        this.element.addEventListener('click', function (e) {
            if(my.forWhat==="Gesture" || my.forWhat==="Sound") {
                return;
            } else {
                e.preventDefault();
                my.fireEvent('click', e);
                my.wavesurfer.fireEvent('region-click', my, e);
            }
        });

        this.element.addEventListener('dblclick', function (e) {
            e.stopPropagation();
            e.preventDefault();
            my.fireEvent('dblclick', e);
            my.wavesurfer.fireEvent('region-dblclick', my, e);
        });

        /* Drag or resize on mousemove. */
        (this.drag || this.resize) && (function () {
            var duration = my.wavesurfer.getDuration();
            var drag;
            var resize;
            var startTime;

            var onDown = function (e) {
                e.stopPropagation();
                var lefttop = my.wavesurfer.drawer.handleEventNew(e);
                startTime = lefttop.left * duration;
                if (e.target.tagName.toLowerCase() == 'handle') {
                    if (e.target.classList.contains('wavesurfer-handle-start')) {
                        resize = 'start';
                    } else {
                        resize = 'end';
                    }
                } else {
                    drag = true;
                }
            };
            var onUp = function (e) {
                if (drag || resize) {
                    drag = false;
                    resize = false;
                    e.stopPropagation();
                    e.preventDefault();

                    my.fireEvent('update-end', e);
                    my.wavesurfer.fireEvent('region-update-end', my, e);
                    
                    saveTranscription();
                }
            };
            var onMove = function (e) {
                if (drag || resize) {
                    var lefttop = my.wavesurfer.drawer.handleEventNew(e);
                    var time = lefttop.left * duration;
                    var delta = time - startTime;
                    startTime = time;
                    
                    var top = calculateTopForTranscription(lefttop.top);

                    // Drag
                    if (my.drag && drag) {
                        my.onDragNew(delta,top);
                    }

                    // Resize
                    if (my.resize && resize) {
                        my.onResize(delta, resize);
                    }
                }
            };

            my.element.addEventListener('mousedown', onDown);
            my.wrapper.addEventListener('mousemove', onMove);
            document.body.addEventListener('mouseup', onUp);

            my.on('remove', function () {
                document.body.removeEventListener('mouseup', onUp);
                my.wrapper.removeEventListener('mousemove', onMove);
            });

            my.wavesurfer.on('destroy', function () {
                document.body.removeEventListener('mouseup', onUp);
            });
        }());
    },

    onDrag: function (delta) {
        this.update({
            start: this.start + delta,
            end: this.end + delta,
            top: this.top
        });
    },
    
    onDragNew: function (delta, top) {
        this.update({
            start: this.start + delta,
            end: this.end + delta,
            top: top
        });
    },

    onResize: function (delta, direction) {
        if (direction == 'start') {
            this.update({
                start: Math.min(this.start + delta, this.end),
                end: Math.max(this.start + delta, this.end)
            });
        } else {
            this.update({
                start: Math.min(this.end + delta, this.start),
                end: Math.max(this.end + delta, this.start)
            });
        }
    }
};

WaveSurfer.util.extend(WaveSurfer.Region, WaveSurfer.Observer);


/* Augment WaveSurfer with region methods. */
WaveSurfer.initRegions = function () {
    if (!this.regions) {
        this.regions = Object.create(WaveSurfer.Regions);
        this.regions.init(this);
    }
};

WaveSurfer.addRegion = function (options) {
    this.initRegions();
    return this.regions.add(options);
};

WaveSurfer.clearRegions = function () {
    this.regions && this.regions.clear();
};

WaveSurfer.enableDragSelection = function (options) {
    this.initRegions();
    this.regions.enableDragSelection(options);
};