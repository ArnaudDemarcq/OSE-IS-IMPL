<html>
	<head>
		<title>
			test
		</title>
	</head>
	<body>
		<div id="inputfields">
			<form action="$endpoint" method="POST">
				#foreach( $item in $input )
					<div class="inputfield">
						<label for="$item.name">$item.name</label><input type="$item.type" name="$item.name" value="$!item.value" id="$item.name"/><br />
					</div>
				#end
				<input type="submit" name="submit" value="" id="submit" />	

			</form>  
		</div>
		<div class="outputfields">
			#foreach( $item in $output )
				<div class="outputfield">
					<label for="$item.name">$item.name</label><input type="$item.type" name="$item.name" value="$!item.value" id="$item.name"/><br />
				</div>
			#end
		</div>
	</body>
</html>