#!/bin/bash

#//tb/1410

cur=`pwd`

src="$cur"/src
build="$cur"/build
archive="$cur"/archive
classes="$build"/classes

package_path=ch/lowres/audio_rxtx/gui

windows_binaries_zip_name=audio_rxtx_1414529893.zip
windows_binaries_uri="https://github.com/7890/jack_tools/raw/master/audio_rxtx/dist/win/$windows_binaries_zip_name"
#dl to (incl. filename)
windows_binaries_zip="/tmp/$windows_binaries_zip_name"

splash_screen_image="$src"/gfx/audio_rxtx_splash_screen.png

icon_image="$src"/gfx/icon.png

#-Xlint:all 

function compile_audio_rxtx()
{
	echo "building audio_rxtx gui application"
	echo "==================================="

	javac -source 1.6 -target 1.6 -classpath "$classes" -sourcepath "$src" -d "$classes" "$src"/$package_path/*.java
	ret=$?
	if [ $ret -ne 0 ]
	then
		echo "error while compiling."
		exit 1
	fi

	echo "start with:"
	echo "java -splash:src/gfx/audio_rxtx_splash_screen.png -Xms1024m -Xmx1024m -cp .:build/classes/ ch.lowres.audio_rxtx.gui.jack_audio_send_GUI"
}

function compile_java_osc
{
	echo "building JavaOSC library (com.illposed.osc)"
	echo "==========================================="
	cp "$archive"/JavaOSC-master.zip "$build"
	cd "$build"
	unzip JavaOSC-master.zip
	cd "$cur"

	PREF="$build"/JavaOSC-master/modules/core/src/main/java

	echo "compiling files in $PREF to direcotry $OUT ..."

	mkdir -p "$classes"
	javac -source 1.6 -target 1.6 -classpath $PREF -sourcepath $PREF -d "$classes" $PREF/com/illposed/osc/*.java
	javac -source 1.6 -target 1.6 -classpath $PREF -sourcepath $PREF -d "$classes" $PREF/com/illposed/osc/utility/*.java
	find "$classes"
}

function build_jar
{
	echo "creating audio_rxtx application jar (audio_rxtx_gui_xxx.jar)"
	echo "============================================================"

	mkdir -p "$classes"/resources
	cp "$archive"/AudioMono.ttf "$classes"/resources

	cp "$src"/gfx/audio_rxtx_about_screen.png "$classes"/resources

	##############################
	if [ ! -e "$windows_binaries_zip" ]
	then
		wget -O "$windows_binaries_zip" "$windows_binaries_uri"
		#cp "/home/srv/source/git/7890/jack_tools/audio_rxtx/dist/win/$windows_binaries_zip_name" "$windows_binaries_zip"
	fi

	cp "$windows_binaries_zip" "$build"
	cd "$build"

	zip_filename="`echo \"$windows_binaries_zip\" | rev | cut -d\"/\" -f1 | rev`"
	unzip "$zip_filename"
	zip_dirname="`echo \"$zip_filename\" | rev | cut -d\".\" -f2- | rev`"
	cd "$zip_dirname"
	rm bin/*.bat
	rm bin/osc*.exe
	rm bin/jack_audio_receive.exe
	rm bin/audio_post_send.exe
	cp -r bin "$classes"/resources
	cp -r doc "$classes"/resources
	cp *.txt "$classes"/resources

	rm -rf audio_rxtx_*

	cp "$icon_image" "$classes"/resources
	cp "$src"/etc/audio_rxtx_gui.properties "$classes"/resources

	echo "Main-Class: ch.lowres.audio_rxtx.gui.jack_audio_send_GUI" > "$build"/Manifest.txt
	echo "" >> "$build"/Manifest.txt

	cd "$classes"

	now=`date +"%s"`

	echo "creating jar..."
	jar cfvm audio_rxtx_gui_"$now".jar "$build"/Manifest.txt ch/ com/ resources/
	ls -l audio_rxtx_gui_"$now".jar
	echo "move audio_rxtx_gui_$now.jar to build dir..."
	mv audio_rxtx_gui_"$now".jar "$build"

	echo "start with"
	echo "java -splash:src/gfx/audio_rxtx_splash_screen.png -Xms1024m -Xmx1024m -jar build/audio_rxtx_gui_$now.jar"

	echo "done."
}

#execute:

mkdir -p "$build"
rm -rf "$build"/*
#rm -f "$build"/*.zip

compile_java_osc
compile_audio_rxtx
build_jar
